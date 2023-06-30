package com.borened.redis.cmd;

import com.borened.redis.RedisDb;
import lombok.Data;

/**
 * 指令上下文
 *
 * @author cch
 * @since 2023/6/30
 */
@Data
public class CmdContext {

    private RedisDb redisDb;

    private String cmd;

    private String[] args;

}
