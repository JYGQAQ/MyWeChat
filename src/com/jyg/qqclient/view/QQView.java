package com.jyg.qqclient.view;

import com.jyg.qqclient.service.ClientConnectServerThread;
import com.jyg.qqclient.service.ManageClientServerThread;
import com.jyg.qqclient.service.UserClientService;
import com.jyg.qqclient.utils.Utility;
import com.jyg.qqserver.service.ManageServerClientThread;
import com.jyg.qqserver.service.ServerConnectClientThread;

public class QQView {

    private boolean loop = true;//控制是否显示菜单
    private String key = "";//接受用户的键盘输入
    private UserClientService userClientService = new UserClientService();

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("=============客户端退出系统=============");
    }

    private void mainMenu() {
        while (loop) {
            System.out.println("===============欢迎登录网络通信系统==============");
            System.out.println("\t\t\t\t 1 登录系统");
            System.out.println("\t\t\t\t 9 退出系统");

            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.print("请输入用户号: ");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码: ");
                    String pwd = Utility.readString(50);

//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    if (userClientService.checkUser(userId, pwd)) {
                        System.out.println("===============欢迎(用户 " + userId + " )==============");
                        while (loop) {
                            System.out.println("===============网络通信系统耳机菜单(用户 " + userId + ")登录==============");
                            System.out.println("\t\t\t 1 显示在线用户列表");
                            System.out.println("\t\t\t 2 群发消息");
                            System.out.println("\t\t\t 3 私聊消息");
                            System.out.println("\t\t\t 4 发送文件");
                            System.out.println("\t\t\t 9 退出系统");
                            System.out.print("请输入你的选择: ");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    System.out.println("\n==================显示在线用户列表=================");
                                    userClientService.onlineList();
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "2":
                                    System.out.println("群发消息");
                                    break;
                                case "3":
                                    System.out.println("私聊消息");
                                    break;
                                case "4":
                                    System.out.println("发送文件");
                                    break;
                                case "9":
                                    loop = false;
                                    userClientService.exit();
                                    System.exit(0);
                                    break;
                            }
                        }
                    } else {
                        System.out.println("=================登录失败================");
                    }
                    break;
                case "9":
                    loop = false;
                    System.out.println("退出系统");
                    break;
            }
        }
    }
}
