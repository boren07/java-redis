package com.borened.redis.db.persistence;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author chengcaihua
 * @description 持久化
 * @since 2024-08-02 16:20
 * @param <T> 要持久化的数据类型,必须是Serializable接口的实现
 */
public interface Persistence<T extends Serializable> {
    /**
     * 将数据持久化到磁盘
     *
     * @param data 要持久化的数据
     * @param file 存储文件
     */
    void store(T data,File file) throws IOException;

    /**
     * 从磁盘加载数据
     * @param file 读取的文件
     * @return 应返回原始存储的数据类型
     * @throws Exception 在加载文件时可能抛出的文件系统异常和反序列化数据时的反序列化异常
     */
    T load(File file) throws Exception;
}
