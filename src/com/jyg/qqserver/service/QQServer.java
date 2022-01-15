package com.jyg.qqserver.service;

import com.jyg.qqclient.service.ManageClientServerThread;
import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import com.jyg.qqcommon.User;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QQServer {
    private ServerSocket serverSocket = null;
    private static ConcurrentHashMap<User, Boolean> validUser = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> offlineMessage = new ConcurrentHashMap<>();

    static {
        validUser.put(new User("100", "123456"), false);
        validUser.put(new User("200", "123456"), false);
        validUser.put(new User("300", "123456"), false);
        validUser.put(new User("至尊宝", "123456"), false);
        validUser.put(new User("紫霞仙子", "123456"), false);
        validUser.put(new User("菩提老祖", "123456"), false);
    }

    public static ConcurrentHashMap<User, Boolean> getValidUser() {
        return validUser;
    }

    public QQServer() {
        System.out.println("服务端在9999端口监听...");
        new SendNewsThread().start();
        try {
            serverSocket = new ServerSocket(9999);

            while (true) {
                Socket socket = serverSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User user = (User)objectInputStream.readObject();

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                Message message = new Message();

                if (checkUser(user)) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCESS);
                    objectOutputStream.writeObject(message);
                    //创建一个线程，和客户端保持通讯，该线程持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user);
                    serverConnectClientThread.start();
                    ManageServerClientThread.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);

                    sendOfflineMessage(user.getUserId());
                } else {
                    System.out.println("用户" + user.getUserId() + "登录失败");
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

    private boolean checkUser(User user) {
        if (!validUser.containsKey(user)) return false;
        if (validUser.get(user) == true) return false;
        validUser.put(user, true);
        return true;
    }

    private void sendOfflineMessage(String userId) {
        if (offlineMessage.containsKey(userId)) {
            sendOfflineCommon(userId);
            sendOfflineFile(userId);
            offlineMessage.remove(userId);
        }
        return ;
    }

    private void sendOfflineCommon(String userId) {
        ArrayList<Message> arrayList = offlineMessage.get(userId);
        for (Message message : arrayList) {
            if (message.getMesType() == MessageType.MESSAGE_COMMON) {
                ServerConnectClientThread serverConnectClientThread = ManageServerClientThread.getServerConnectClientThread(userId);
                Socket socket = serverConnectClientThread.getSocket();
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void sendOfflineFile(String userId) {
        ArrayList<Message> arrayList = offlineMessage.get(userId);
        for (Message message : arrayList) {
            if (message.getMesType() == MessageType.MESSAGE_FILE) {
                try {
                    message.setSender(InetAddress.getLocalHost().getHostAddress());
                    ServerConnectClientThread serverConnectClientThread = ManageServerClientThread.getServerConnectClientThread(userId);
                    Socket socket = serverConnectClientThread.getSocket();
                    OutputStream outputStream1 = socket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream1);
                    objectOutputStream.writeObject(message);
                    ServerSocket serverSocket = new ServerSocket(10001);
                    Socket socket2 = serverSocket.accept();
                    OutputStream outputStream = socket2.getOutputStream();
                    String fileName = message.getFileName();
                    FileInputStream fileinputStream2 = new FileInputStream(new File("e:/temp" + fileName));
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = fileinputStream2.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    socket2.close();
                    serverSocket.close();
                    fileinputStream2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ConcurrentHashMap<String, ArrayList<Message>> getOfflineMessage() {
        return offlineMessage;
    }
}
