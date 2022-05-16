package com.jyg.qqserver.service;

import com.jyg.qqclient.service.ManageClientServerThread;
import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import com.jyg.qqcommon.User;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Socket getSocket() {
        return socket;
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
                        ConcurrentHashMap<User, Boolean> validUser = QQServer.getValidUser();
                        validUser.put(user, false);
                        break;
                    case MESSAGE_COMMON:
                        if (!(isOnList(message.getReceiver()))) {
                            ConcurrentHashMap<String, ArrayList<Message>> offlineMessage = QQServer.getOfflineMessage();
                            if (!(offlineMessage.containsKey(message.getReceiver()))) {
                                offlineMessage.put(message.getReceiver(), new ArrayList<>());
                            }
                            ArrayList<Message> arrayList = offlineMessage.get(message.getReceiver());
                            arrayList.add(message);
                            break;
                        }
                        ServerConnectClientThread serverConnectClientThread = ManageServerClientThread.getServerConnectClientThread(message.getReceiver());
                        Socket socket = serverConnectClientThread.socket;
                        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream1.writeObject(message);
                        break;
                    case MESSAGE_COMMON_GROUP:
                        String onlineList = ManageServerClientThread.getOnlineList();
                        String[] list = onlineList.split(" ");
                        for (String userId : list) {
                            if (userId.equals(user.getUserId())) continue;
                            ServerConnectClientThread serverConnectClientThread1 = ManageServerClientThread.getServerConnectClientThread(userId);
                            ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(serverConnectClientThread1.socket.getOutputStream());
                            objectOutputStream2.writeObject(message);
                        }
                        break;
                    case MESSAGE_FILE:
                        String fileName = message.getFileName();
                        String userIp = message.getUserIp();
                        String receiver = message.getReceiver();
                        Socket socket1  = new Socket(userIp, 10000);
                        InputStream inputStream = socket1.getInputStream();
                        byte[] buffer = new byte[1024];
                        int length = 0;
                        FileOutputStream fileOutputStream = new FileOutputStream(new File("e:/temp" + fileName));
                        while ((length = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.close();
                        inputStream.close();
                        socket1.close();

                        if (!(isOnList(message.getReceiver()))) {
                            ConcurrentHashMap<String, ArrayList<Message>> offlineMessage = QQServer.getOfflineMessage();
                            if (!(offlineMessage.containsKey(message.getReceiver()))) {
                                offlineMessage.put(message.getReceiver(), new ArrayList<>());
                            }
                            ArrayList<Message> arrayList = offlineMessage.get(message.getReceiver());
                            arrayList.add(message);
                            break;
                        }

                        message.setSender(InetAddress.getLocalHost().getHostAddress());
                        ServerConnectClientThread serverConnectClientThread1 = ManageServerClientThread.getServerConnectClientThread(receiver);
//                        if (serverConnectClientThread1 == null) {
//                            System.out.println("用户不在线");
//                            break;
//                        }
                        Socket socket3 = serverConnectClientThread1.socket;
                        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(socket3.getOutputStream());
                        objectOutputStream2.writeObject(message);
                        ServerSocket serverSocket = new ServerSocket(10001);
                        Socket socket2 = serverSocket.accept();
                        OutputStream outputStream = socket2.getOutputStream();
                        FileInputStream fileinputStream2 = new FileInputStream(new File("e:/temp" + fileName));
                        while ((length = fileinputStream2.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        outputStream.close();
                        socket2.close();
                        serverSocket.close();
                        fileinputStream2.close();
                        break;
                    case QUERY_WORD:
                        String content1 = message.getContent();
                        QQServer.query(content1);
                        Message message2 = new Message();
                        message2.setMesType(MessageType.QUERY_WORD);
                        message2.setContent(" " + QQServer.query(content1) + " ");
                        message2.setSendTime(LocalDateTime.now());
                        message2.setReceiver(message.getSender());
                        message2.setSender("服务器");
                        ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(this.socket.getOutputStream());
                        objectOutputStream3.writeObject(message2);
                        break;

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isOnList(String userId) {
        String onlineList = ManageServerClientThread.getOnlineList();
        String[] list = onlineList.split(" ");
        for (String e : list) {
            if (e.equals(userId))
                return true;
        }
        return false;
    }
}
