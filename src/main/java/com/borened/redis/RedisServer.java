package com.borened.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static com.borened.redis.RedisDb.DB_ARR;

/**
 * redis服务
 *
 * @author cch
 * @since 2023/6/30
 */
public class RedisServer {

    private static RedisInfo redisInfo;

    private static List<RedisDb> redisDbs;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private RedisServer() {
    }

    public static void start(){
        LOCK.lock();
        try {
            if (redisDbs != null) {
                throw new RedisException("redis server is started...");
            }
            redisDbs = new ArrayList<>(RedisDb.DB_ARR.length);
            for (int index : DB_ARR) {
                redisDbs.add(new RedisDb(index));
            }
            redisInfo = new RedisInfo();
            redisInfo.setRedisDbs(redisDbs);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            LOCK.unlock();
        }

    }

    public static void stop() {

    }


    public static RedisInfo getRedisInfo(){
        if (redisInfo == null) {
            throw new RedisException("redis server is stopping...");
        }
        return redisInfo;
    }

    public static RedisDb getDb(int index) {
        if (redisDbs == null) {
            throw new RedisException("redis server is stopping...");
        }
        if (index<0 || index>= DB_ARR.length){
            throw new RedisException("index should be between 0 and 15...");
        }
        return redisDbs.get(index);
    }

}
