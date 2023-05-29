package org.example.Message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    //消息内容
    private String content;
    private String recipient;//接收方
}
