package com.jyg.qqserver.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import com.jyg.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
                System.out.println("服务器和客户端 " + user.getUserId() + " 保持通信，读取信息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)objectInputStream.readObject();
                MessageType mesType = message.getMesType();
                switch (mesType) {
                    case MESSAGE_GET_ONLINE_LIST:
                        String content = ManageServerClientThread.getOnlineList();
//                        ConcurrentHashMap<User, Boolean> validUser = QQServer.getValidUser();
//                        Set<Map.Entry<User, Boolean>> entries = validUser.entrySet();
//                        for (Map.Entry<User, Boolean> entry : entries) {
//                            if (entry.getValue()) {
//                                if (content.length() != 0) {
//                                    content += " ";
//                                }
//                                content += entry.getKey().getUserId();
//                            }
//                        }

                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
