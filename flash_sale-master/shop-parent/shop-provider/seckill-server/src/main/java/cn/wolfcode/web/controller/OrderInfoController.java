package cn.wolfcode.web.controller;

import cn.wolfcode.common.constants.CommonConstants;
import cn.wolfcode.common.web.CommonCodeMsg;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.common.web.anno.RequireLogin;
import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.mq.MQConstant;
import cn.wolfcode.mq.OrderMessage;
import cn.wolfcode.redis.SeckillRedisKey;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.util.DateUtil;
import cn.wolfcode.util.UserUtil;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lanxw
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderInfoController {
    @Autowired
    private ISeckillProductService seckillProductService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 线程 500 循环次数 10
     *  32.1 qps
     * Redis优化之后
     * 214 qps
     */
    @RequestMapping("/doSeckill")
    @RequireLogin //自定义的登录注解，表示该方法只能登录之后访问
    public Result<String> doSecKill(@RequestParam("time") Integer time,@RequestParam("seckillId") Long seckillId, HttpServletRequest request){
        //1.判断是否处于抢购的时间
        //先获得抢购商品
        SeckillProductVo seckillProductVo = seckillProductService.findFromCache(time, seckillId);
        //判断该商品的抢购时间是否合法，用自定义的工具类
        boolean legalTime = DateUtil.isLegalTime(seckillProductVo.getStartDate(), seckillProductVo.getTime());
//        if(!legalTime){
//            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
//        }
        //2.一个用户只能抢购一个商品
        //先从请求中获取token
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        //根据token从redis中获取手机号
        String phone = UserUtil.getUserPhone(redisTemplate,token);
        //如果一个用户在该场次已经抢购了该商品，就在这个orderInfo数据库中插入该信息，如果在该数据库中查找到该信息，则不能在抢购，否则可以抢购
//        OrderInfo orderInfo =  orderInfoService.findByPhoneAndSeckillId(phone,seckillId);
        //优化方案，从redis中set集合中读取电话号码是否已经存在
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
        if(redisTemplate.opsForSet().isMember(orderSetKey,phone)){
            //提示重复下单
            return Result.error(SeckillCodeMsg.REPEAT_SECKILL);
        }
        //3.保证库存数量足够
        if(seckillProductVo.getStockCount() <= 0){
            //提示库存不足
            return Result.error(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }
        //使用Redis来过滤筛选访问业务层方秒杀法的请求数量
        String seckillStockCountKey = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
        //原子性递减
        Long remainCount = redisTemplate.opsForHash().increment(seckillStockCountKey, String.valueOf(seckillId), -1);
        //只有让在redis中的库存递减之后不为负数的线程请求才可以向下进行，访问业务层秒杀方法。原子性递减会导致最后的值是宇哥负数
        if(remainCount < 0){
            return Result.error(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }

        //4.扣减数据库的库存
        //5.创建秒杀订单
        //上述两个步骤是原子性操作
//        OrderInfo orderInfo = orderInfoService.doSeckill(phone, seckillProductVo);
        //使用MQ进行异步下单
        //异步下单为什么用synSend(),因为秒杀请求时同步发送给消息中间件的
        OrderMessage orderMessage = new OrderMessage(time,seckillId,token,Long.valueOf(phone));
        rocketMQTemplate.syncSend(MQConstant.ORDER_PEDDING_TOPIC, orderMessage);
        return Result.success("成功进入秒杀队列，请耐心等待结果");
    }

    /**
     * 查询订单详情的控制层方法
     * @param orderNo 订单编号
     * @return
     */
    @RequestMapping("/find")
    @RequireLogin
    public Result<OrderInfo> find(String orderNo,HttpServletRequest request){
        //订单信息
        OrderInfo orderInfo = orderInfoService.findByOrderInfo(orderNo);
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        String phone = UserUtil.getUserPhone(redisTemplate,token);
        if(!phone.equals(String.valueOf(orderInfo.getUserId()))){
            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
        }
        return Result.success(orderInfo);
    }
}
