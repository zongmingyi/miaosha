package org.example.controller;


import org.example.Message.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@Controller
public class chatController {
    //存放所有连接的客户端
    //键值为建立的websocket 会话（session）,value值表示标识
    private static final Map<WebSocketSession,String> clients = new HashMap<>();

    //利用注入的该对象向客户端发送消息
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 聊天函数
     * @param chatMessage 发送的消息
     * @param principal 安全规则
     */
    @MessageMapping("/chat")//用于处理客户端发送的消息
    public void handleCharMessage(@Payload ChatMessage chatMessage, Principal principal){
        String sender = principal.getName();//发送方名称
        String content = chatMessage.getContent();//消息内容
        String recipient = chatMessage.getRecipient();//接受方

        //发送给所有人
        if(recipient == null){
            //为什么传递一个ChatMessage对象中用发送方呢，因为要告诉接收方，发送信息时其发送的地址
//            simpMessagingTemplate.convertAndSend("/topic/public",new ChatMessage(sender,content));
        }else {
         //发给指定用户
            for(WebSocketSession client: clients.keySet()){
                String id = clients.get(client);
                if(id.equals(recipient)){
                    //因为建立的连接时websocket服务器与客户端的连接，所以要利用这个连接来发送数据
                    try {
                        client.sendMessage(new TextMessage(new ChatMessage(sender,content).toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //当这个用户发完消息之后就结束循环
                    break;
                }
            }
        }
    }

    @EventListener//处理websocket的建立与断开时间
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        WebSocketSession session = (WebSocketSession) event.getSource();//getSource获得websocket连接底层的会话对象
        String username = session.getPrincipal().getName();//建立连接的提供的认证凭据，如用户名、密码
        clients.put(session,username);
//        simpMessagingTemplate.convertAndSend("/topic/public",new ChatMessage("System",username + "joined"));
    }

    @EventListener
    public void handleWebSocketDisconnectLister(SessionDisconnectEvent event){
        WebSocketSession session = (WebSocketSession) event.getSource();
        String username = clients.get(session);
        clients.remove(session);
//        simpMessagingTemplate.convertAndSend("/topic/public",new ChatMessage("System",username + "exit"));
    }

}
