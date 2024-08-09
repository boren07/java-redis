package com.borened.redis;

import com.borened.redis.config.ConfigManager;

import java.util.List;

/**
 * @author cch
 * @since 2023/6/30
 */

public class RedisConfigException extends RedisException {
    public static final String ERROR_MSG = "please check your config file 【%s】, reason is ：【%s】";

    private List<String> errorKeys;

    public RedisConfigException(String message, Exception e) {
        super(String.format(ERROR_MSG, ConfigManager.CONFIG_FILE_NAME,message),e);
    }
    public RedisConfigException(String message) {
        super(ERROR_MSG, ConfigManager.CONFIG_FILE_NAME,message);
    }
}
