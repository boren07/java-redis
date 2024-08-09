package com.borened.redis.config;

import lombok.Data;

import java.util.Properties;

/**
 * @author chengcaihua
 * @description 服务器配置
 * @since 2024-08-02 14:12
 */
@Data
public class ConfigProperties  {

    private String host;

    private String port;

    private String persistenceMode;

}
