package com.borened.redis.event;

/**
 * 改变类型枚举
 *
 * @author cch
 * @since 2023/6/30
 */
public enum KeyChangeTypeEnum {
    //添加
    ADD,
    UPDATE,
    DEL,
    EXPIRED;
}
