package com.jyg.qqclient.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class MessageClientService {

    public static void sendMessageToAll(String content, String senderId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMMON_GROUP);
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d H:m:s");
        System.out.println(dateTimeFormatter.format(message.getSendTime()) + " " + senderId + " 对 大家伙 说 " + content);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageClientServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToOne(String content, String senderId, String getterId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMMON);
        message.setSender(senderId);
        message.setReceiver(getterId);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d H:m:s");
        System.out.println(dateTimeFormatter.format(message.getSendTime()) + " " + senderId + " 对 " + getterId + " 说 " + content);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageClientServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
