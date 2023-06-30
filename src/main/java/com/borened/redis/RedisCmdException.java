package com.borened.redis;

/**
 * @author cch
 * @since 2023/6/30
 */

public class RedisCmdException extends RedisException {

    public RedisCmdException(String message) {
        super("redis ops exception , please check your cmd , reason is ：【%s】",message);
    }
}
