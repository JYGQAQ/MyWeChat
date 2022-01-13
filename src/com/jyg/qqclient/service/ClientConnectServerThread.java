package com.jyg.qqclient.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
