package cn.wolfcode.mq;

import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
@RocketMQMessageListener(consumerGroup = "pendingGroup",topic = MQConstant.ORDER_PEDDING_TOPIC)
public class OrderPendingQueueListener implements RocketMQListener<OrderMessage> {
    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private ISeckillProductService seckillProductService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Override
    public void onMessage(OrderMessage orderMessage) {
        OrderMQResult result = new OrderMQResult();
        //返回消息消费结果
        result.setToken(orderMessage.getToken());
        //消息的标签
        String tag;
        try {
            SeckillProductVo vo = seckillProductService.findFromCache(orderMessage.getTime(),orderMessage.getSeckillId());
            OrderInfo orderInfo = orderInfoService.doSeckill(String.valueOf(orderMessage.getUserPhone()),vo);
            //秒杀消息创建订单成功之后设置的信息
            result.setOrderNo(orderInfo.getOrderNo());
            tag = MQConstant.ORDER_RESULT_SUCCESS_TAG;
            //创建订单成功之后发送延时消息
            Message<OrderMQResult> message = MessageBuilder.withPayload(result).build();
            rocketMQTemplate.syncSend(MQConstant.ORDER_PAY_TIMEOUT_TOPIC,message,3000,MQConstant.ORDER_PAY_TIMEOUT_DELAY_LEVEL);
        }catch (Exception E){
            E.printStackTrace();
            //秒杀消息创建订单失败之后设置的内容
            result.setCode(SeckillCodeMsg.SECKILL_ERROR.getCode());
            result.setMsg(SeckillCodeMsg.SECKILL_ERROR.getMsg());
            result.setTime(orderMessage.getTime());
            result.setSeckillId(orderMessage.getSeckillId());
            tag = MQConstant.ORDER_RESULT_FAIL_TAG;
        }
        //不管成功还是失败的消息都向这个topic中发送，这个消息队列是用来给websocket服务器消费的
        rocketMQTemplate.syncSend(MQConstant.ORDER_RESULT_TOPIC+":"+tag,result);
    }
}
