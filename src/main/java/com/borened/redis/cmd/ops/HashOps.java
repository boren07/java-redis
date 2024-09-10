package com.borened.redis.cmd.ops;

import com.borened.redis.cmd.CmdOpsContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cch
 * @since 2023/7/4
 */
@SuppressWarnings("SpellCheckingInspection")
public class HashOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("hdel","hexists","hget","hmget","hgetall","hincrby","hkeys","hlen","hset","hmset","hsetnx","hvals","hscan"));
    }

    @Override
    public String exec(CmdOpsContext ctx) {
        return null;
    }

    @Override
    public RedisDataType type() {
        return RedisDataType.HASH;
    }
}
