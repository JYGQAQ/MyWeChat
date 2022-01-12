package com.jyg.qqserver.service;

import com.jyg.qqclient.service.ManageClientServerThread;
import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import com.jyg.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class QQServer {
    private ServerSocket serverSocket = null;

    public QQServer() {
        System.out.println("服务端在9999端口监听...");
        try {
            serverSocket = new ServerSocket(9999);

            while (true) {
                Socket socket = serverSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User user = (User)objectInputStream.readObject();

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                Message message = new Message();

                if (user.getUserId().equals("100") && user.getPasswd().equals("123456")) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCESS);
                    objectOutputStream.writeObject(message);
                    //创建一个线程，和客户端保持通讯，该线程持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user);
                    serverConnectClientThread.start();
                    ManageServerClientThread.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);
                } else {
                    message.setMesType(MessageType.MESSAGE_lOGIN_FAIL);
                    objectOutputStream.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭服务器监听
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
