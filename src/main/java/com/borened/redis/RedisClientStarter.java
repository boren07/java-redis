package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;

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
        CmdOpsExecutor opsExecutor = CmdOpsExecutor.getInstance();
        //注册观察者
        System.out.println("欢迎来到Java-redis,请输入命令并按Enter键执行");
        System.out.println("请先选择数据库！输入'select [0-15]'");
        Socket socket = new Socket("127.0.0.1", 8888);
        OutputStream out = socket.getOutputStream();
        // 准备服务端的输入信息
        BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(() -> {
            while (true) {
                String result = null;
                try {
                    if ( (result = buf.readLine()) !=null) {
                        System.out.println(result);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.print(">");
            }
        }).start();

        while (true){
            System.out.print(">");
            Scanner input = new Scanner(System.in);
            String cmd = input.nextLine();
            System.out.println("你输入了命令："+ cmd);
            out.write(cmd.getBytes());
        }

    }


}
