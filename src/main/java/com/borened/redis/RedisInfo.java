package com.borened.redis;

import lombok.Data;

import java.util.List;

/**
 * redis数据类
 *
 * @author cch
 * @since 2023/6/30
 */

@Data
public class RedisInfo {

    private int memorySize;

    private String os;

    private int clientNum;


    private List<RedisDb> redisDbs;


    public RedisInfo() {
        this.memorySize = 0;
        this.os = System.getProperty("os.name");
        this.clientNum = 0;
    }
}
