package com.borened.redis;

import com.borened.redis.config.ConfigProperties;
import com.borened.redis.db.DatabaseEngine;
import com.borened.redis.observer.KeyObservable;
import com.borened.redis.util.SingletonFactory;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.locks.ReentrantLock;

import static com.borened.redis.RedisDb.DB_ARR;

/**
 * redis服务
 *
 * @author cch
 * @since 2023/6/30
 */
public class RedisServer {

    public static volatile boolean isRunning = false;

    private static RedisInfo redisInfo;

    private static ConfigProperties config;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private RedisServer() {
    }

    public static void start(ConfigProperties configProperties){
        LOCK.lock();
        try {
            config = configProperties;
            if (isRunning) {
                throw new RedisException("redis server is started...");
            }
            //初始化数据库引擎
            SingletonFactory.registerSingleton(new DatabaseEngine());
            redisInfo= SingletonFactory.getSingleton(RedisInfo.class);
            //注册观察者
            SingletonFactory.getSingleton(KeyObservable.class).addObserver(redisInfo);
            isRunning = true;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            LOCK.unlock();
        }

    }

    /**
     * 服务器关闭时应该执行的必要事件
     */
    public static void stop() {
        LOCK.lock();
        try {
            DatabaseEngine engine = SingletonFactory.getSingleton(DatabaseEngine.class);
            try {
                DatabaseEngine.rdbPersistence.store(redisInfo, DatabaseEngine.RDB_DATA_DIR);
            } catch (IOException e) {
                System.err.println("rdb save error...");
                e.printStackTrace();
            }
            try {
                DatabaseEngine.aofPersistence.store(engine.getAofCacheCommands(), DatabaseEngine.AOF_DATA_DIR);
                engine.getAofCacheCommands().forEach(List::clear);
            } catch (IOException e) {
                System.err.println("aof save error...");
                e.printStackTrace();
            }
            isRunning = false;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            LOCK.unlock();
        }
    }


    public static RedisInfo getRedisInfo(){
        if (redisInfo == null) {
            throw new RedisException("redis server is stopping...");
        }
        return redisInfo;
    }

    public static RedisDb getDb(int index) {
        List<RedisDb> redisDbs = getRedisInfo().getRedisDbs();
        if (redisDbs == null) {
            throw new RedisException("redis server is stopping...");
        }
        if (index<0 || index>= DB_ARR.length){
            throw new RedisException("index should be between 0 and 15...");
        }
        return redisDbs.get(index);
    }

}
