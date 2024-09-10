package com.borened.redis;

import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.event.KeyChangeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * redis数据类.整个redis库的访问主类
 *
 * @author cch
 * @since 2023/6/30
 */

@Data
public class RedisInfo implements Observer, Serializable {


    private final String os;

    private List<RedisDb> redisDbs;
    private volatile AtomicInteger clientNum;
    private volatile AtomicInteger memorySize;


    public RedisInfo() {
        this.memorySize = new AtomicInteger(0);
        this.os = System.getProperty("os.name");
        this.clientNum = new AtomicInteger(0);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof KeyChangeEvent) {
            KeyChangeEvent changeEvent = (KeyChangeEvent) arg;
            RedisDb redisDb = changeEvent.getRedisDb();
            if (changeEvent.getType() == KeyChangeTypeEnum.ADD) {
                //todo 暂时简单计算一下占用的内存，后续完善。
                memorySize.getAndAdd(changeEvent.getNewValue().toString().getBytes().length) ;
                redisDb.getKeyNum().getAndIncrement();
            }
            else if (changeEvent.getType() == KeyChangeTypeEnum.UPDATE) {
                memorySize.getAndAdd(changeEvent.getNewValue().toString().getBytes().length - changeEvent.getOldValue().toString().getBytes().length);
            }
            else if (changeEvent.getType() == KeyChangeTypeEnum.DEL) {
                memorySize.getAndAdd(- changeEvent.getNewValue().toString().getBytes().length);
                redisDb.getKeyNum().getAndDecrement();
            }
            else if (changeEvent.getType() == KeyChangeTypeEnum.EXPIRED) {
                memorySize.getAndAdd(- changeEvent.getNewValue().toString().getBytes().length);
                redisDb.getKeyNum().getAndDecrement();
                redisDb.getExpireNum().getAndIncrement();
            }
        }
    }

    @Override
    public String toString() {
        return "RedisInfo{" +
                "os='" + os + '\'' +
                ", redisDbs=" + redisDbs +
                ", clientNum=" + clientNum +
                ", memorySize=" + memorySize +
                '}';
    }
}
