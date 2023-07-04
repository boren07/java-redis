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

    private RedisDb redisDb;

    private String key;

}
