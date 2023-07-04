package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author cch
 * @since 2023/7/4
 */
public class TestRedis {

    @Before
    public void before(){
        RedisServer.start();
    }

    @Test
    public void test1() throws InterruptedException {
        CmdOpsExecutor opsExecutor = CmdOpsExecutor.getInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        CountDownLatch countDownLatch = new CountDownLatch(200);
        for (int j = 0; j < 100; j++) {
            int i = j;
            executorService.submit(() -> {
                opsExecutor.cmdExecute(String.format("set name%s value%s", i, i));
                countDownLatch.countDown();
            });
            executorService.submit(() -> {
                opsExecutor.cmdExecute(String.format("get name%s value%s", i, i));
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println(RedisServer.getDb(0));
        System.out.println(RedisServer.getDb(0).getData().size());
    }
}
