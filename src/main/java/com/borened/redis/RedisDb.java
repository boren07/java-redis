package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;
import com.borened.redis.cmd.ops.RedisDataType;
import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.event.KeyExpiredEvent;
import com.borened.redis.observer.KeyObservable;
import com.borened.redis.util.SingletonFactory;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class RedisDb implements Serializable {

    public static final int[] DB_ARR = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

    private final int index;

    private final Map<String,MetaData> data;

    private volatile AtomicInteger keyNum;

    private volatile AtomicInteger expireNum;


    public RedisDb(int index) {
        this.index = index;
        this.data =  new HashMap<>();
        this.keyNum = new AtomicInteger(0);
        this.expireNum = new AtomicInteger(0);
    }

    @Data
    public static class MetaData implements Serializable{

        private Object data;

        private long expireAt;

        private RedisDataType type;

        //即将到期的未来任务
        private transient ScheduledFuture<?> expiringFuture;

        public MetaData(Object data) {
            this.data = data;
            this.expireAt = -1;
        }

    }


    public void registerKeyExpireEvent(String key,long seconds,long expireAt) {
        MetaData metaData0 = data.get(key);
        if (metaData0 == null) {
            throw new RedisException("key not exists, can not register expire event");
        }
        if (metaData0.getExpiringFuture() != null) {
            metaData0.getExpiringFuture().cancel(true);
        }
        if (seconds<0){
            metaData0.setExpireAt(-1);
            return;
        }
        long now = System.currentTimeMillis();
        metaData0.setExpireAt(expireAt);
        if (expireAt <= now) {
            SingletonFactory.getSingleton(KeyObservable.class).notifyObservers(KeyChangeEvent.expiredOf(this, key, metaData0.getData()));
            return;
        }
        //未来到期,使用异步任务去删除
        ScheduledFuture<?> expiringFuture = CmdOpsExecutor.getInstance().schedule(() -> {
            MetaData metaData = data.remove(key);
            SingletonFactory.getSingleton(KeyObservable.class).notifyObservers(KeyChangeEvent.expiredOf(this, key, metaData.getData()));
        }, seconds);
        metaData0.setExpiringFuture(expiringFuture);
    }
}
