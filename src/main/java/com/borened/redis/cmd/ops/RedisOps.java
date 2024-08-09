package com.borened.redis.cmd.ops;

import com.borened.redis.RedisDb;
import com.borened.redis.cmd.CmdOpsContext;
import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.observer.KeyObservable;

import java.util.Observable;
import java.util.Set;

/**
 * redis操作指令
 *
 * @author cch
 * @since 2023/6/30
 */
public interface RedisOps {
    /**
     * 支持那些命令
     * @return 支持的set集合
     */
    Set<String> supports();

    /**
     * 执行命令
     * @return 执行结果
     */
    String exec(CmdOpsContext ctx);


    default RedisDataType type() {
        return null;
    }
    default void notifyKeyObservers(CmdOpsContext cmdContext, String key, RedisDb.MetaData oldVal, Object newVal) {
        //todo 更新观察者机制
        KeyObservable observable = cmdContext.getKeyObservable();
        observable.notifyObservers(oldVal !=null ?
                KeyChangeEvent.updateOf(cmdContext.getRedisDb(), key, oldVal.getData(), newVal) : KeyChangeEvent.addOf(cmdContext.getRedisDb(), key, newVal));
    }
}
