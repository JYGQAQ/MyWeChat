package com.jyg.qqserver.service;

import com.jyg.qqclient.utils.Utility;
import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

public class SendNewsThread extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println("请输入服务器要推送的新闻/消息, 输入 “exit”表示退出推送服务");
            String news = Utility.readString(100);
            if (news.equals("exit")) break;
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_COMMON_GROUP);
            message.setSendTime(LocalDateTime.now());
            System.out.println("服务器推送消息给所有人 说： " + news);

            //遍历当前所有的通信线程，得到socket，发送message

            String onlineList = ManageServerClientThread.getOnlineList();
            String[] list = onlineList.split(" ");
            for (int i = 0; i < list.length; i++) {
                ServerConnectClientThread serverConnectClientThread = ManageServerClientThread.getServerConnectClientThread(list[i]);
                Socket socket = serverConnectClientThread.getSocket();
                try {
                    message.setReceiver(list[i]);
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
