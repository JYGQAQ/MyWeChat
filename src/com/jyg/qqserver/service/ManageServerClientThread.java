package com.jyg.qqserver.service;

import com.sun.security.ntlm.Server;

import java.util.HashMap;

public class ManageServerClientThread {

    private static HashMap<String, ServerConnectClientThread> map = new HashMap<>();

    public static void addServerConnectClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        map.put(userId, serverConnectClientThread);
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return map.get(userId);
    }
}
