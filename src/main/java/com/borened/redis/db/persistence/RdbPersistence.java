package com.borened.redis.db.persistence;

import com.borened.redis.RedisInfo;

import java.io.*;
import java.nio.file.Files;

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
        return (RedisInfo) data;
    }
}
