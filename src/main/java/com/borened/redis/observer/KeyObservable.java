package com.borened.redis.observer;

import java.util.Observable;

/**
 * key变化观察者单例
 *
 * @author cch
 * @since 2023/6/30
 */
public class KeyObservable extends Observable{
    @Override
    protected synchronized void setChanged() {
        super.setChanged();
    }


    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}
