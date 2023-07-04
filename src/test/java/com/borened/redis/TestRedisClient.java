package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;

import java.util.Scanner;

/**
 * 主程序
 *
 * @author cch
 * @since 2023/6/30
 */
public class TestRedisClient {

    public static void main(String[] args) {
        run();
    }


    public static void run(){
        RedisInfo redisInfo = RedisServer.getRedisInfo();
        redisInfo.getClientNum().getAndIncrement();
        CmdOpsExecutor opsExecutor = CmdOpsExecutor.getInstance();
        //注册观察者
        System.out.println("欢迎来到Java-redis,请输入命令并按Enter键执行");
        System.out.println("请先选择数据库！输入'select [0-15]'");
        while (true){
            try {
                System.out.print(">");
                Scanner input = new Scanner(System.in);
                String cmd = input.nextLine();
                System.out.println("你输入了命令："+ cmd);
                String result = opsExecutor.cmdExecute(cmd);
                System.out.println(result);
            } catch (RedisException e) {
                System.err.println(e.getMessage());
            }
        }

    }


}
