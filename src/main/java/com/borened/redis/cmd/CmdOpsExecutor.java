package com.borened.redis.cmd;

import com.borened.redis.RedisCmdException;
import com.borened.redis.RedisServer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 操作策略
 *
 * @author cch
 * @since 2023/6/30
 */
public class CmdOpsExecutor {

    private CmdOpsExecutor() {
    }

    private static CmdOpsExecutor opsExecutor;

    private List<RedisOps> redisOpsList;
    private ScheduledExecutorService scheduledExecutorService;

    public static CmdOpsExecutor getInstance(){
        if (opsExecutor == null) {
            opsExecutor = new CmdOpsExecutor();
            opsExecutor.redisOpsList = Arrays.asList(new StringOps());
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("redis-expires-ops-%d").build();
            opsExecutor.scheduledExecutorService = new ScheduledThreadPoolExecutor(10,namedThreadFactory);
        }
        return opsExecutor;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public String cmdExecute(int dbIndex, String cmd) {
        if (cmd == null){
            throw new RedisCmdException("cmd not be null");
        }
        String[] cmdArr = cmd.split("\\s+");
        if (cmdArr.length<=1) {
            throw new RedisCmdException("cmd length must least 2！");
        }
        CmdContext ctx = new CmdContext();
        ctx.setRedisDb(RedisServer.getDb(dbIndex));
        ctx.setCmd(cmdArr[0]);
        ctx.setArgs(Arrays.copyOfRange(cmdArr,1,cmdArr.length));
        for (RedisOps redisOps : redisOpsList) {
            if (redisOps.support().contains(cmdArr[0].toLowerCase())) {
                String result;
                try {
                    result = redisOps.exec(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RedisCmdException("cmd syntax inner error！please see doc...");
                }
                return result;
            }
        }
        throw new RedisCmdException("unsupported cmd '"+ cmd +"'");
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
}
