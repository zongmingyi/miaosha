package cn.wolfcode.ws;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/{token}")
@Component
public class OrderWSServer {
    //用来存储客户端的连接，key是token，value是建立的连接
    public static ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token){
        System.out.println("浏览器和服务器建立连接");
        //建立和浏览器的会话的映射关系
        clients.put(token,session);
    }
//    @OnMessage
//    public void onMessage(@PathParam("token")String token,String msg){
//        System.out.println("收到客户端标识为："+token+",发送的消息："+msg);
//    }
    @OnClose
    public void onClose(@PathParam("token") String token){
        System.out.println("浏览器与服务器断开连接:"+token);
        clients.remove(token);
    }
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }
}
