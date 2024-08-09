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
        super(String.format("redis exception ,%s",message));
    }

    public RedisException(String message, Throwable cause) {
        super(String.format("redis exception ,%s",message), cause);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

    protected RedisException(String message,Object... messageArgs) {
        this(String.format(message , messageArgs));
    }


}
