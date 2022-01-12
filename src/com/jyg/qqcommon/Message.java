package com.jyg.qqcommon;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sender;//发送者
    private String receiver;//接受者
    private String content;//消息内容
    private LocalDateTime sendTime;//发送时间
    private MessageType mesType;//消息类型[可以再接口定义消息类型]

    public MessageType getMesType() {
        return mesType;
    }

    public void setMesType(MessageType mesType) {
        this.mesType = mesType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}
