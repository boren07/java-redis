package com.borened.redis;

/**
 * 命令语法异常
 * @author cch
 * @since 2023/6/30
 */

public class RedisCmdException extends RedisException {

    public RedisCmdException(String message) {
        super("please check your cmd syntax ！ error reason is ：【%s】",message);
    }
}
