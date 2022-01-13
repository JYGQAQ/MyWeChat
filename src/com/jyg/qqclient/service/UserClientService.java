package com.jyg.qqclient.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import com.jyg.qqcommon.User;
import com.jyg.qqserver.service.ManageServerClientThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class UserClientService {

    private User user = new User();
    private Socket socket;


    //关闭连接
    public void exit() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //向服务器端请求在线用户列表
    public void onlineList() {
        Message message = new Message();
        message.setSender(user.getUserId());
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_LIST);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageClientServerThread.getClientConnectServerThread(user.getUserId()).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据userId 和 pwd 到服务器验证该用户是否合法
    public boolean checkUser(String userId, String pwd) {
        boolean res = false;
        //创建User对象
        user.setUserId(userId);
        user.setPasswd(pwd);

        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(user);

            //读入从服务端回送的Message对象
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Message message = (Message)objectInputStream.readObject();

            if (message.getMesType() == MessageType.MESSAGE_LOGIN_SUCCESS) {
                //创建一个和服务器端保持通信的线程 ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();
                //这里为了后面的客户端的扩展，我们将线程放入到集合中管理
                ManageClientServerThread.addClientConnectServerThread(user.getUserId(), clientConnectServerThread);

                res = true;

            } else {
                //关闭socket
                objectInputStream.close();
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
