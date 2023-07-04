package com.borened.redis.cmd;

import com.borened.redis.*;
import com.borened.redis.cmd.ops.*;
import com.borened.redis.observer.KeyChangeObservableSingleton;
import com.borened.redis.util.StrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * 操作策略
 *
 * @author cch
 * @since 2023/6/30
 */
public class CmdOpsExecutor {

    private CmdOpsExecutor() {
    }

    private static volatile CmdOpsExecutor opsExecutor;
    private volatile boolean isDoing = false;

    private List<RedisOps> redisOpsList;
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService mainThreadPool;
    private MainThread mainThread;

    public static CmdOpsExecutor getInstance(){
        if (opsExecutor == null) {
            synchronized (CmdOpsExecutor.class) {
                if (opsExecutor == null) {
                    opsExecutor = new CmdOpsExecutor();
                    opsExecutor.redisOpsList = Arrays.asList(new StringOps(),new ListOps(),new SetOps(),new ZSetOps(),new HashOps(),
                            new CommonOps(),new ConnectionOps());
                    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                            .setNameFormat("redis-expires-ops-%d").build();
                    opsExecutor.scheduledExecutorService = new ScheduledThreadPoolExecutor(10, namedThreadFactory);
                    opsExecutor.mainThreadPool = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                            .setNameFormat("redis-main-%d").build());
                    opsExecutor.mainThread = new MainThread(null,null);
                }
            }
        }
        return opsExecutor;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public ScheduledFuture<?> schedule (Runnable runnable,long seconds){
        //使用异步任务去删除
        return scheduledExecutorService.schedule(runnable, seconds, TimeUnit.SECONDS);
    }
    public String cmdExecute( String cmd) {
        if (cmd == null) {
            throw new RedisCmdException("cmd not be null");
        }
        String[] cmdArr = cmd.split("\\s+");
        if (cmdArr.length <= 1) {
            throw new RedisCmdException("cmd length must least 2！");
        }
        String clientId = StrUtil.getClientId();
        RedisDb redisDb = RedisServer.getDb(0);
        if (CmdOpsContext.clientDbContextCache.containsKey(clientId)) {
            redisDb = CmdOpsContext.clientDbContextCache.get(clientId);
        }
        CmdOpsContext ctx = new CmdOpsContext();
        ctx.setRedisDb(redisDb);
        ctx.setCmd(cmdArr[0]);
        ctx.setArgs(Arrays.copyOfRange(cmdArr, 1, cmdArr.length));
        ctx.setKeyObservable(KeyChangeObservableSingleton.getInstance());
        for (RedisOps redisOps : redisOpsList) {
            if (redisOps.supports().contains(cmdArr[0].toLowerCase())) {
                return actualExecute(ctx, redisOps);
            }
        }
        throw new RedisCmdException("unsupported cmd '" + cmd + "'");
    }


    private String actualExecute(CmdOpsContext ctx,RedisOps ops) {
        String result = StrUtil.NIL;
        System.out.printf("当前线程:%s , 主线程工作状态：%s%n",Thread.currentThread().getName(),isDoing);
        if (isDoing) {
            try {
                System.out.printf("当前线程:%s , 主线程处理中，进入等待重试。。。%n",Thread.currentThread().getName());
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RedisException("unknown error... plz see console",e);
            }
            actualExecute(ctx,ops);
        }
        else {
            isDoing = true;
            mainThread.setCtx(ctx);
            mainThread.setRedisOps(ops);
            try {
                Future<String> future = mainThreadPool.submit(mainThread);
                //阻塞当前线程
                result = future.get();
                System.out.printf("当前线程:%s , 成功调度工作线程.执行结果：%s%n",Thread.currentThread().getName(),result);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RedisCmdException("cmd syntax inner error！please see doc...");
            }finally {
                isDoing = false;
            }
        }
        return result;
    }

    private static class ThreadFactoryBuilder {
        private String nameFormat;
        public ThreadFactoryBuilder setNameFormat(String nameFormat) {
            this.nameFormat = nameFormat;
            return this;
        }

        public ThreadFactory build() {
            return new MyThreadFactory(nameFormat);
        }


        private static class MyThreadFactory implements ThreadFactory{
            private int threadNumber;
            private final String nameFormat;

            public MyThreadFactory(String nameFormat) {
                this.nameFormat = nameFormat;
            }
            @Override
            public Thread newThread(Runnable r) {
                threadNumber++;
                return new Thread(r, String.format(nameFormat, threadNumber));
            }
        }

    }

    private static class MainThread implements Callable<String> {

        private RedisOps redisOps;
        private CmdOpsContext ctx;

        public void setRedisOps(RedisOps redisOps) {
            this.redisOps =  redisOps ;
        }

        public void setCtx(CmdOpsContext ctx) {
            this.ctx = ctx;
        }

        public MainThread(RedisOps redisOps, CmdOpsContext ctx) {
            this.redisOps  = redisOps;
            this.ctx = ctx;
        }

        @Override
        public String call() {
            System.out.printf("工作线程:%s , 执行命令上下文：%s%n",Thread.currentThread().getName(),ctx);
            if (redisOps != null) {
                return redisOps.exec(ctx);
            }
            return null;
        }
    }
}
