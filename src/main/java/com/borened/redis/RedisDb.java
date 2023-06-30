package com.borened.redis;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class RedisDb {

    public static final int[] DB_ARR = new int[16];

    private int index;

    private Map<String,Object> data;

    private int keyNum;

    private int expireNum;


    public RedisDb(int index) {
        this.index = index;
        this.data =  new HashMap<>();
        this.keyNum = 0;
        this.expireNum = 0;
    }

    public int getKeyNum() {
        return data.size();
    }
}
