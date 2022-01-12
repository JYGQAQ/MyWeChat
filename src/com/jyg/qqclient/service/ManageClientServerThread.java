package com.jyg.qqclient.service;

import java.util.HashMap;

public class ManageClientServerThread {
    //多个线程放入到一个HashMap集合，key就是用户id，value 即使线程
    private static HashMap<String, ClientConnectServerThread> map = new HashMap<>();

    //放入线程
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        map.put(userId, clientConnectServerThread);
    }

    //取出线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return map.get(userId);
    }
}
