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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QQServer {
    private ServerSocket serverSocket = null;
    private static ConcurrentHashMap<User, Boolean> validUser = new ConcurrentHashMap<>();

    static {
        validUser.put(new User("100", "123456"), false);
        validUser.put(new User("200", "123456"), false);
        validUser.put(new User("300", "123456"), false);
        validUser.put(new User("至尊宝", "123456"), false);
        validUser.put(new User("紫霞仙子", "123456"), false);
        validUser.put(new User("菩提老祖", "123456"), false);
    }

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

                if (checkUser(user)) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCESS);
                    objectOutputStream.writeObject(message);
                    //创建一个线程，和客户端保持通讯，该线程持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user);
                    serverConnectClientThread.start();
                    ManageServerClientThread.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);
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

}
