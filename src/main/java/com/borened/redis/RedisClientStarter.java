package com.borened.redis;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 主程序
 *
 * @author cch
 * @since 2023/6/30
 */
@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
public class RedisClientStarter {

    public static void main(String[] args) throws IOException {
        run();
    }


    public static void run() throws IOException {
        //注册观察者
        System.out.println("欢迎来到Java-redis,请输入命令并按Enter键执行");
        Scanner scanner = new Scanner(System.in);
        Socket socket = null;
        String addr = "";
        boolean isConnected = false;
        while (!isConnected) {
            System.out.println("请输入服务器地址:");
            addr = scanner.nextLine();
            try {
                socket = new Socket(addr.split(":")[0], Integer.parseInt(addr.split(":")[1]));
            } catch (Exception e) {
                System.err.println("无法连接服务器！"+ addr);
                continue;
            }
            System.out.println("连接成功！");
            isConnected = true;
        }
        String cmdLog = addr+">";
        System.out.println(cmdLog+"请先选择数据库！输入'select [0-15]'");
        OutputStream out = socket.getOutputStream();
        // 准备服务端的输入信息
        BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(() -> {
            while (true) {
                String result = null;
                try {
                    if ((result = buf.readLine()) != null) {
                        System.out.println(result);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("网络连接出现错误"+e);
                }
                System.out.print(cmdLog);
            }
        }).start();

        while (true) {
            System.out.print(cmdLog);
            Scanner input = new Scanner(System.in);
            String cmd = input.nextLine();
            //System.out.println(cmdLog+"你输入了命令：" + cmd);
            out.write(cmd.getBytes());
        }

    }


}
