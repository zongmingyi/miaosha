//package org.example.ws;
//
//
//
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import javax.websocket.OnClose;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
///**
// * 聊天室的服务端程序
// * @author Administrator
// *
// */
////声明websocket某个服务端的地址
//@ServerEndpoint(value = "/charRoomServer/{param}")
//@Component
//public class ChatRoomServer {
//
//    private boolean firstFlag=true;
//    private Session session;
//    private String userName;
//
//    //发送人id
//    private String userId;
//    //key代表此次客户端的userId，value代表此次连接对象
//    private static final HashMap<String, Object> connectMap=new HashMap<String, Object>();
//    //保存所有用户昵称信息
//    //key是session的id，value是用户昵称
//    private static final HashMap<String, String> userMap=new HashMap<String, String>();
//    //服务端收到客户端的连接请求，连接成功后会执行此方法
//    @OnOpen
//    public void start(@PathParam(value = "param") String param, Session session) {
//        this.session=session;
//        this.userId=param; //接收参数
//        connectMap.put(param,this);
//    }
//
//    //客户端发来的信息，服务端接收
//    @OnMessage              //接收人userId
//    public void chat(String clientMessage,Session session) {
//        //firstFlag为true是第一次进入，第二次进入之后设为false
//        ChatRoomServer client=null;
//        if(firstFlag) {
//            this.userName=clientMessage;
//            //将新进来的用户保存到用户map
//            userMap.put(session.getId(), userName);
//
//            try {
//                if("超级管理员".equals(userId)){
//
//                }else{
//                    //构造发送给客户端的提示信息
//                    String message=htmlMessage("大白机器人：","亲爱的"+userId+"，您想了解点儿啥？");
//                    client=(ChatRoomServer) connectMap.get(userId);
//                    //给对应的web端发送一个文本信息
//                    client.session.getBasicRemote().sendText(message);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            firstFlag=false;
//        }else {
//            System.err.println("clientMessage:"+userName);
//            //给对方发消息
//            String message1=htmlMessage(userId,clientMessage);
//            client  = (ChatRoomServer) connectMap.get(userName);
//            if(client!=null){
//                try {
//                    client.session.getBasicRemote().sendText(message1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            //给自己窗口发消息
//            String message2=htmlMessage(userId,clientMessage);
//            client  = (ChatRoomServer) connectMap.get(userId);
//            try {
//                client.session.getBasicRemote().sendText(message2);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            //这是将前台用户发送的消息存数据库并标记为未读，和上面通信没关系
//            if("超级管理员".equals(userId)){
//
//            }else{
//                Map map=new HashMap();
//                map.put("account",userId);
//                map.put("message",clientMessage);
//                map.put("addtime",new Date());
//
//            }
//        }
//    }
//
//    /**
//     * 前台js的ws.close事件，会触发后台的标注onClose的方法
//     */
//    @OnClose
//    public void close() {
//        userMap.remove(session.getId());
//        connectMap.remove(userId);
//    }
//    /**
//     * 渲染页面，把信息构造好标签再发送
//     */
//    public String htmlMessage(String userName,String message) {
//        StringBuffer stringBuffer=new StringBuffer();
//        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        stringBuffer.append("<article>");
//        stringBuffer.append("<span>"+sf.format(new Date())+"</span>");
//        stringBuffer.append("<div class='avatar'>");
//        stringBuffer.append("<h3>"+userName+"</h3>");
//        stringBuffer.append("</div>");
//        stringBuffer.append("<div class='msg'>");
//        stringBuffer.append("<div class='tri'></div>");
//        stringBuffer.append("<div class='msg_inner'>"+message+"</div>");
//        stringBuffer.append("</div>");
//        stringBuffer.append("</article>");
//        //这里拼接了消息发送人的userId，在前台进行截取字符串接收发送人的userId
//        stringBuffer.append("|"+userName);
//        return stringBuffer.toString();
//    }
//}
