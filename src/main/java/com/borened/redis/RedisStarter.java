package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;

import java.util.Scanner;

/**
 * 主程序
 *
 * @author cch
 * @since 2023/6/30
 */
public class RedisStarter {

    public static void main(String[] args) {
        run();
    }


    public static void run(){
        RedisServer.start();
        CmdOpsExecutor opsExecutor = CmdOpsExecutor.getInstance();
        System.out.println("请输入命令,按Enter键执行");
        while (true){
            try {
                System.out.print(">");
                Scanner input = new Scanner(System.in);
                String cmd = input.nextLine();
                System.out.println("你输入了命令："+ cmd);
                String result = opsExecutor.cmdExecute(0, cmd);
                System.out.println(result);
            } catch (RedisException e) {
                System.err.println(e.getMessage());
            }
        }

    }
}
