package cn.wolfcode.mq;

import cn.wolfcode.ws.OrderWSServer;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(consumerGroup = "OrderResultGroup",topic = MQConstants.ORDER_RESULT_TOPIC)
public class OrderResultQueueListener implements RocketMQListener<OrderMQResult> {
    @Override
    public void onMessage(OrderMQResult orderMQResult) {
        //找到客户端
        Session session = null;
        int count  = 3;
        //因为可能会第一次拿不到，所以进行一个循环，查询多次
        while(count-- > 0){
            session = OrderWSServer.clients.get(orderMQResult.getToken());
            if(session != null){
                try {
                    session.getBasicRemote().sendText(JSON.toJSONString(orderMQResult));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
