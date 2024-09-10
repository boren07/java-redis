package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;
import com.borened.redis.config.ConfigManager;
import com.borened.redis.config.ConfigProperties;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 主程序
 *
 * @author cch
 * @since 2023/6/30
 */
public class RedisServerStarter {

    public static void main(String[] args) throws IOException {
        //绑定程序关闭时的钩子
        Runtime.getRuntime().addShutdownHook(new Thread(RedisServer::stop));
        run();
    }


    public static void run() throws IOException {
        ConfigProperties configProperties = ConfigManager.getConfigProperties();
        RedisServer.start(configProperties);
        RedisInfo redisInfo = RedisServer.getRedisInfo();

        CmdOpsExecutor opsExecutor = CmdOpsExecutor.getInstance();
        System.out.println("Java-redis启动成功！！！");
        // 服务器在8888端口上监听
        ServerSocket server = new ServerSocket(Integer.parseInt(configProperties.getPort()));
        while (true) {
            Socket client = null;
            try {
                System.out.println("等待客户端连接...");
                // 得到连接，程序进入到阻塞状态
                 client = server.accept();
                System.out.println("客户端已连接-"+ client.getInetAddress().toString());
                //todo 统计客户端数量,断开连接更新
                redisInfo.getClientNum().getAndIncrement();
                PrintStream out = new PrintStream(client.getOutputStream());
                // 准备接收客户端的输入信息
                InputStream is = client.getInputStream();
                boolean flag = true;
                byte[] bytes = new byte[1024];
                while (flag) {
                    // 接收客户端发送的内容
                    int len = is.read(bytes);
                    String input = new String(bytes,0,len);
                    System.out.println("收到客戶端信息："+input);
                    if ("".equals(input)) {
                        out.println("input is empty!");
                    } else {
                        // 回应信息
                        String result = null;
                        try {
                            result = opsExecutor.clientCmdExecute(input);
                        } catch (RedisException e) {
                            result = e.getMessage();
                            System.err.println(e.getMessage());
                        }
                        out.println(result);
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                client.close();
                redisInfo.getClientNum().getAndDecrement();
            }
        }
    }

}
