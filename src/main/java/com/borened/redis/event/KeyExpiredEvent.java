package com.borened.redis.event;


import lombok.Data;

/**
 * key到期事件
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class KeyExpiredEvent extends KeyEvent{

    private Long expiredAt;



}
