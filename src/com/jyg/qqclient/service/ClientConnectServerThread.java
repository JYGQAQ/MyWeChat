package com.jyg.qqclient.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ClientConnectServerThread extends Thread {
    private Socket socket;//持有和Server通信的socket
    private boolean loop = true;

    public ClientConnectServerThread(Socket socket_) {
        socket = socket_;
    }

    @Override
    public void run() {
        //因为Thread需要在后台和服务器保持通信，使用while
        while (loop) {
            try {
                System.out.println("客户端线程，等待从服务器端发送的消息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)objectInputStream.readObject();//如果服务器没有发送Message对象,线程会阻塞在这里
                MessageType mesType = message.getMesType();
                switch (mesType) {
                    case MESSAGE_RETURN_LIST:
                        String content = message.getContent();
                        String[] onlineList = content.split(" ");
                        for (int i = 0; i < onlineList.length; i++) {
                            System.out.println("用户: " + onlineList[i]);
                        }
                        break;
                    case MESSAGE_COMMON:
                        String content1 = message.getContent();
                        String sender = message.getSender();
                        String receiver = message.getReceiver();
                        LocalDateTime sendTime = message.getSendTime();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d H:m:s");
                        System.out.println(dateTimeFormatter.format(sendTime) + " " + sender + " 对 " + receiver + " 说 " + content1);
                        break;
                    case MESSAGE_COMMON_GROUP:
                        content1 = message.getContent();
                        sender = message.getSender();
                        sendTime = message.getSendTime();
                        dateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d H:m:s");
                        System.out.println(dateTimeFormatter.format(sendTime) + " " + sender + " 对 大家伙 说 " + content1);
                        break;
                    case MESSAGE_FILE:
                        sender = message.getSender();
//                        System.out.println("是否要接受文件, yes or no");
//                        Scanner scanner = new Scanner(System.in);
//                        String next = scanner.next();
//                        if (! next.equals("yes")) break;
                        Socket socket = new Socket(sender, 10001);
                        InputStream inputStream = socket.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream("e:/" + message.getReceiver() + message.getFileName());
                        byte[] buffer = new byte[1024];
                        int length = 0;
                        while ((length = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                        socket.close();
                        break;
                    case QUERY_WORD:
                        String content2 = message.getContent();
                        System.out.println("服务器：该单词翻译为" + content2);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
