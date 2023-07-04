package com.borened.redis.event;

import com.borened.redis.RedisDb;
import lombok.Data;

/**
 * key改变事件
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class KeyChangeEvent extends KeyEvent {

    private KeyChangeTypeEnum type;

    private Object oldValue;
    private Object newValue;


    private KeyChangeEvent(RedisDb redisDb,String key,KeyChangeTypeEnum type, Object newValue) {
        super(redisDb,key);
        this.type = type;
        this.newValue = newValue;
    }
    private KeyChangeEvent(RedisDb redisDb,String key,Object oldValue,Object newValue) {
        super(redisDb,key);
        this.type = KeyChangeTypeEnum.UPDATE;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    private KeyChangeEvent(RedisDb redisDb,String key, Object newValue) {
        super(redisDb,key);
        this.type = KeyChangeTypeEnum.EXPIRED;
        this.newValue = newValue;
    }
    public static KeyChangeEvent updateOf(RedisDb redisDb,String key,Object oldValue,Object newValue){
        return new KeyChangeEvent(redisDb, key,oldValue, newValue);
    }
    public static KeyChangeEvent expiredOf(RedisDb redisDb,String key, Object expValue){
        return new KeyChangeEvent(redisDb, key, expValue);
    }

    public static KeyChangeEvent addOf(RedisDb redisDb,String key,Object newValue){
        return new KeyChangeEvent(redisDb, key,KeyChangeTypeEnum.ADD, newValue);
    }
    public static KeyChangeEvent delOf(RedisDb redisDb,String key, Object rmValue){
        return new KeyChangeEvent(redisDb, key,KeyChangeTypeEnum.DEL, rmValue);
    }
}
