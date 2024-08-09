package com.borened.redis.config;

import com.borened.redis.RedisConfigException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author chengcaihua
 * @description 配置管理器
 * @since 2024-08-02 14:16
 */
public class ConfigManager {

    public static final String CONFIG_FILE_NAME = "jadis.properties";
    public static final String CONFIG_DIR = "config";

    public static ConfigProperties configProperties;
    static {
        configProperties = loadConfigProperties();
    }

    public static ConfigProperties getConfigProperties() {
        return configProperties;
    }
    public static ConfigProperties loadConfigProperties() {
        Properties properties;
        try {
            properties = readPropertiesFile();
        } catch (IOException e) {
            throw new RedisConfigException("config file not exists", e);
        }
        if (properties.isEmpty()) {
            throw new RedisConfigException("config file is empty");
        }
        return buildProperties(properties);
    }

    private static ConfigProperties buildProperties(Properties properties) {
        ConfigProperties configProperties = new ConfigProperties();
        Field[] declaredFields = ConfigProperties.class.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String key = field.getName();
            String value = properties.getProperty(key);
            if (value != null) {
                try {
                    field.set(configProperties, value);
                } catch (Exception e) {
                    throw new RedisConfigException("config parse error `key:" + key + "value:" + value + "`",e);
                }
            }
        }
        validateProperties(configProperties);
        return configProperties;
    }

    public static Properties readPropertiesFile() throws IOException {
        Properties properties = new Properties();
        //加载自定义配置文件
        Path jarPath = Paths.get(ConfigManager.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));

        Path cfgFilePath0 = jarPath.getParent().resolve(CONFIG_FILE_NAME);
        try {
            // 优先读取jar同级目录配置文件
            properties.load(Files.newInputStream(cfgFilePath0));
        } catch (IOException e) {
            // 备选jar同级config目录配置文件
            Path cfgFilePath1 = jarPath.getParent().resolve(CONFIG_DIR+ File.separator + CONFIG_FILE_NAME);
            try {
                properties.load(Files.newInputStream(cfgFilePath1));
            } catch (IOException ex) {
                //jar同级目录没有配置文件，则读取classpath配置文件
                properties.load(ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
            }

        }
        return properties;
    }


    private static void validateProperties(ConfigProperties configProperties) {
        if (configProperties.getHost() == null) {
            throw new RedisConfigException("host is null");
        }
        if (configProperties.getPort() == null) {
            throw new RedisConfigException("port is null");
        }
        if (configProperties.getPersistenceMode() == null) {
            throw new RedisConfigException("persistenceMode is null");
        }
        if (!"rdb".equals(configProperties.getPersistenceMode()) && !"aof".equals(configProperties.getPersistenceMode())) {
            throw new RedisConfigException("persistenceMode is invalid");
        }
    }
}
