package com.borened.redis.cmd;

import com.borened.redis.RedisDb;
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

    private String cmd;

    private String[] args;

    private Observable keyObservable;

    public static ConcurrentHashMap<String,RedisDb> clientDbContextCache = new ConcurrentHashMap<>();

}
