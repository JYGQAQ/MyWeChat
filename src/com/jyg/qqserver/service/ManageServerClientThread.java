package com.jyg.qqserver.service;

import com.sun.security.ntlm.Server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ManageServerClientThread {

    private static HashMap<String, ServerConnectClientThread> map = new HashMap<>();

    public static void addServerConnectClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        map.put(userId, serverConnectClientThread);
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return map.get(userId);
    }

    public static String getOnlineList() {
        Set<String> strings = map.keySet();
        String content = "";

        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            if (content.length() != 0) {
                content += " ";
            }
            content += iterator.next();
        }
        return content;
    }
}
