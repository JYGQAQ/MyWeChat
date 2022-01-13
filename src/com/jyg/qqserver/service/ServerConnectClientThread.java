package com.jyg.qqserver.service;

import com.jyg.qqclient.service.ManageClientServerThread;
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
    private boolean loop = true;

    public ServerConnectClientThread(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }

    @Override
    public void run() {
        while (loop) {
            try {
                System.out.println("服务器和客户端 " + user.getUserId() + " 保持通信，读取信息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)objectInputStream.readObject();
                MessageType mesType = message.getMesType();
                switch (mesType) {
                    case MESSAGE_GET_ONLINE_LIST:
                        String content = ManageServerClientThread.getOnlineList();
                        Message message1 = new Message();
                        message1.setMesType(MessageType.MESSAGE_RETURN_LIST);
                        message1.setContent(content);
                        message.setReceiver(user.getUserId());
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(message1);
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
                    case MESSAGE_CLIENT_EXIT:
                        loop = false;
                        objectInputStream.close();
                        socket.close();
                        ManageServerClientThread.deleteServerConnectClientThread(user.getUserId());
                    case MESSAGE_COMMON:
                        ServerConnectClientThread serverConnectClientThread = ManageServerClientThread.getServerConnectClientThread(message.getReceiver());
                        Socket socket = serverConnectClientThread.socket;
                        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream1.writeObject(message);
                    case MESSAGE_COMMON_GROUP:
                        String onlineList = ManageServerClientThread.getOnlineList();
                        String[] list = onlineList.split(" ");
                        for (String userId : list) {
                            if (userId.equals(user.getUserId())) continue;
                            ServerConnectClientThread serverConnectClientThread1 = ManageServerClientThread.getServerConnectClientThread(userId);
                            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(serverConnectClientThread1.socket.getOutputStream());
                            objectOutputStream2.writeObject(message);
                        }

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
