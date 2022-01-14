package com.jyg.qqclient.service;

import com.jyg.qqcommon.Message;
import com.jyg.qqcommon.MessageType;
import com.jyg.qqserver.service.ManageServerClientThread;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileClientService {

    public static void sendFileToOne(String senderId, String receiveId, File file) {
        if (!file.exists()) {
            System.out.println("文件不存在!");
        }
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE);
        message.setSender(senderId);
        message.setReceiver(receiveId);
        message.setFileName(file.getName());
        try {
            message.setUserIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ClientConnectServerThread clientConnectServerThread = ManageClientServerThread.getClientConnectServerThread(senderId);
        Socket socket = clientConnectServerThread.getSocket();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);

            ServerSocket serverSocket = new ServerSocket(10000);
            Socket socket1 = serverSocket.accept();
            OutputStream outputStream = socket1.getOutputStream();
            System.out.println(outputStream.getClass());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length = 0;
            int sum = 0;
            while ((length = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                sum += length;
                System.out.println(length);
            }
            System.out.println("sum = " + sum);
            fileInputStream.close();
            outputStream.close();
            socket1.close();
            serverSocket.close();
            System.out.println(senderId + " 给 " + receiveId + " 发送文件" + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
