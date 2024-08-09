package com.borened.redis.util;

import com.borened.redis.RedisInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 简单的单例bean工厂
 * @author cch
 * @since 2023/7/4
 */
public class SingletonFactory {

    private static final  Map<Class<?>, Object> SINGLETON_MAP = new ConcurrentHashMap<>();

    /**
     * 获取单例Bean,基于饿汉模式初始化.
     * @param type bean的类型
     * @return
     * @param <T> bean实例
     */
    public static <T> T getSingleton(Class<T> type) {
        Object instance = SINGLETON_MAP.get(type);
        if (instance != null) {
            return (T) instance;
        }
        synchronized (type) {
            instance = SINGLETON_MAP.get(type);
            if (instance == null) {
                try {
                    instance = type.newInstance();
                    SINGLETON_MAP.putIfAbsent(type, instance);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return (T) instance;
        }
    }

    /**
     * 注册bean到工厂,注意如果存在则会覆盖
     * @param instance
     * @param <T>
     */
    public static <T> void registerSingleton(T instance) {
        SINGLETON_MAP.put(instance.getClass(), instance);
    }

    public static boolean exist(Class<?> clazz) {
        return SINGLETON_MAP.containsKey(clazz);
    }
}
