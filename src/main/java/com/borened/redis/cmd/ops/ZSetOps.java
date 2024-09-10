package com.borened.redis.cmd.ops;

import com.borened.redis.cmd.CmdOpsContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cch
 * @since 2023/7/4
 */
public class ZSetOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("zadd","zcard","zcount","zincrby","zlexcount","zrange","zrank","zrem","zscore","zscan"));
    }

    @Override
    public String exec(CmdOpsContext ctx) {
        return null;
    }

    @Override
    public RedisDataType type() {
        return RedisDataType.ZSET;
    }
}
