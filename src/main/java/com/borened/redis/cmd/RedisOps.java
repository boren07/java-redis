package com.borened.redis.cmd;

import java.util.Set;

/**
 * redis操作指令
 *
 * @author cch
 * @since 2023/6/30
 */
public interface RedisOps {

    Set<String> support();

    String exec(CmdContext ctx);
}
