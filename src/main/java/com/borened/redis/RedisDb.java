package com.borened.redis;

import com.borened.redis.cmd.CmdOpsExecutor;
import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.observer.KeyChangeObservableSingleton;
import lombok.Data;

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
public class RedisDb {

    public static final int[] DB_ARR = new int[16];

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
    public static class MetaData{

        private Object data;

        private long expireAt;

        //即将到期的未来任务
        private transient ScheduledFuture<?> expiringFuture;

        public MetaData(Object data) {
            this.data = data;
            this.expireAt = -1;
        }

    }


    public void registerKeyExpireEvent(String key,long seconds) {
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
        metaData0.setExpireAt(System.currentTimeMillis() + (seconds*1000));
        //使用异步任务去删除
        ScheduledFuture<?> expiringFuture = CmdOpsExecutor.getInstance().schedule(() -> {
            MetaData metaData = data.remove(key);
            KeyChangeObservableSingleton.getInstance().notifyObservers(KeyChangeEvent.expiredOf(this, key, metaData.getData()));
        }, seconds);
        metaData0.setExpiringFuture(expiringFuture);
    }
}
