package com.borened.redis.db.persistence;

import com.borened.redis.RedisDb;
import com.borened.redis.RedisInfo;
import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.observer.KeyObservable;
import com.borened.redis.util.SingletonFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author chengcaihua
 * @description
 * @since 2024-08-02 16:24
 */
public class RdbPersistence implements Persistence<RedisInfo> {


    @Override
    public void store(RedisInfo data, File file) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
        outputStream.writeObject(data);
        outputStream.close();
        //todo 压缩文件

    }

    @Override
    public RedisInfo load(File file) throws Exception {
        if (file.length() == 0){
            return null;
        }
        ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(file.toPath()));
        Object data = inputStream.readObject();
        inputStream.close();
        RedisInfo redisInfo = (RedisInfo) data;
        List<RedisDb> redisDbs = redisInfo.getRedisDbs();
        for (RedisDb redisDb : redisDbs) {
            Set<String> expiredKeys = new HashSet<>();
            redisDb.getData().forEach((key, metaData)->{
                if (metaData.getExpireAt()<=System.currentTimeMillis()) {
                    expiredKeys.add(key);
                    SingletonFactory.getSingleton(KeyObservable.class).notifyObservers(KeyChangeEvent.expiredOf(redisDb, key, metaData.getData()));
                }
            });
            expiredKeys.forEach(k->redisDb.getData().remove(k));
        }
        return redisInfo;
    }
}
