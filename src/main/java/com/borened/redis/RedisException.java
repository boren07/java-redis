package com.borened.redis;

/**
 * redis 异常
 *
 * @author cch
 * @since 2023/6/30
 */
public class RedisException extends RuntimeException{

    public RedisException() {
    }

    public RedisException(String message) {
        super(String.format("redis exception reason is ：【%s】",message));
    }

    public RedisException(String message, Throwable cause) {
        super(String.format("redis exception reason is ：【%s】",message), cause);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

    protected RedisException(String message,String messageArgs) {
        super(String.format(message , messageArgs));
    }

}
