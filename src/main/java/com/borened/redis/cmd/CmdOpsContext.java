package com.borened.redis.cmd;

import com.borened.redis.RedisDb;
import com.borened.redis.observer.KeyObservable;
import lombok.Data;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令操作上下文
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class CmdOpsContext {

    private RedisDb redisDb;
    /**
     * 命令类型
     */
    private String cmd;

    private String[] args;

    private KeyObservable keyObservable;
    /**
     * 客户端会话,连接id-访问的数据库
     */
    public static ConcurrentHashMap<String,RedisDb> clientSession = new ConcurrentHashMap<>();

}
