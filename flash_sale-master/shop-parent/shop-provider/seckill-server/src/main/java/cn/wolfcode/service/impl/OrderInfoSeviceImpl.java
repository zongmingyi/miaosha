package cn.wolfcode.service.impl;

import cn.wolfcode.common.exception.BusinessException;
import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.mapper.OrderInfoMapper;
import cn.wolfcode.mapper.PayLogMapper;
import cn.wolfcode.mapper.RefundLogMapper;
import cn.wolfcode.redis.SeckillRedisKey;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.util.IdGenerateUtil;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by wolfcode-lanxw
 */
@Service
public class OrderInfoSeviceImpl implements IOrderInfoService {
    @Autowired
    private ISeckillProductService seckillProductService;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private PayLogMapper payLogMapper;
    @Resource
    private RefundLogMapper refundLogMapper;

    @Override
    public OrderInfo findByPhoneAndSeckillId(String phone, Long seckillId) {
        return orderInfoMapper.findByPhoneAndSeckillId(phone,seckillId);
    }

    @Override
    @Transactional //事务注解
    public OrderInfo doSeckill(String phone, SeckillProductVo seckillProductVo) {
        //4.扣减数据库秒杀商品的库存
        int rows = seckillProductService.decrStockCount(seckillProductVo.getId());
        if(0 == rows){
            //说明update语句执行结果影响行数为0，表示stock_count>0条件并不满足，说明数据库库存不足
            throw new BusinessException(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }
        //5.创建秒杀订单
        OrderInfo orderInfo = createOrderInfo(phone,seckillProductVo);
        System.out.println("抢到商品的用户id"+orderInfo.getUserId());
        //在Redis中设置set集合没存储的是抢到秒杀商品的用胡的手机号码
        //这样key就是SECKILL_ORDER_SET：秒杀商品id,内容是电话号码
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillProductVo.getId()));
        redisTemplate.opsForSet().add(orderSetKey,phone);
        return orderInfo;
    }

    @Override
    public OrderInfo findByOrderInfo(String orderNo) {
        return orderInfoMapper.find(orderNo);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        //到了超时时间之后，查看付款状态，如果未付款，则取消订单。判断订单是否处于未付款状态
        if(OrderInfo.STATUS_ARREARAGE.equals(orderInfo.getStatus())){
            //修改订单状态,更新订单状态为因超时而取消，而不是真正去删除
            int row = orderInfoMapper.updateCancelStatus(orderNo, OrderInfo.STATUS_TIMEOUT);
            //影响行数为0时，表示已主动取消订单或完成支付，后续库存回补不在进行
            if(0 == row){
                return;
            }
            //真实库存回补
            seckillProductService.incrStockCount(orderInfo.getSeckillId());
            //预库存回补
            seckillProductService.synStockToRedis(orderInfo.getSeckillTime(),orderInfo.getSeckillId());
        }
    }

    private OrderInfo createOrderInfo(String phone, SeckillProductVo seckillProductVo) {
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(seckillProductVo,orderInfo);
        orderInfo.setUserId(Long.parseLong(phone));//用户ID
        orderInfo.setCreateDate(new Date());//创建时间
        orderInfo.setDeliveryAddrId(1L);//收货地址id
        orderInfo.setSeckillDate(seckillProductVo.getStartDate());//秒杀日期
        orderInfo.setSeckillTime(seckillProductVo.getTime());//秒杀场次
        orderInfo.setOrderNo(String.valueOf(IdGenerateUtil.get().nextId()));//订单编号
        orderInfo.setSeckillId(seckillProductVo.getId());//秒杀商品id
        orderInfoMapper.insert(orderInfo);
        return orderInfo;
    }
}
