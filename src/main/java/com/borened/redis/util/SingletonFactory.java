package com.borened.redis.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cch
 * @since 2023/7/4
 */
public class SingletonFactory {

    private static final  Map<Class<?>, Object> SINGLETON_MAP = new ConcurrentHashMap<>();


    public static <T> T getSingleton(Class<T> type) {
        synchronized (type) {
            Object instance = SINGLETON_MAP.get(type);
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

}
