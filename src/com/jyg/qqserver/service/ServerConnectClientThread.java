package com.jyg.qqserver.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private User user;

    public ServerConnectClientThread(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
