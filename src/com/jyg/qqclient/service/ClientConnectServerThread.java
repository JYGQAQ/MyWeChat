package com.jyg.qqclient.service;

import com.jyg.qqcommon.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread {
    private Socket socket;//持有和Server通信的socket

    public ClientConnectServerThread(Socket socket_) {
        socket = socket_;
    }

    @Override
    public void run() {
        //因为Thread需要在后台和服务器保持通信，使用while
        while (true) {
            try {
                System.out.println("客户端线程，等待从服务器端发送的消息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)objectInputStream.readObject();//如果服务器没有发送Message对象,线程会阻塞在这里

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
