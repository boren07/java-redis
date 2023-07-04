package com.borened.redis.observer;

import java.util.Observable;

/**
 * key变化观察者单例
 *
 * @author cch
 * @since 2023/6/30
 */
public class KeyChangeObservableSingleton {

    private KeyChangeObservableSingleton() {
    }
    /**
     * 和饿汉模式相比，这边不需要先实例化出来，注意这里的 volatile，它是必须的
     */
    private static volatile Observable instance = null;

    public static Observable getInstance() {
        if (instance == null) {
            // 加锁
            synchronized (KeyChangeObservableSingleton.class) {
                // 这一次判断也是必须的，不然会有并发问题
                if (instance == null) {
                    instance = new Observable();
                }
            }
        }
        return instance;
    }
}
