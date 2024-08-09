package com.borened.redis.event;

import com.borened.redis.RedisDb;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * key事件
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyEvent {

    /**
     * 数据库
     */
    private RedisDb redisDb;
    /**
     * 操作key
     */
    private String key;


}
