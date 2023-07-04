package com.borened.redis.cmd.ops;

import com.borened.redis.cmd.CmdOpsContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cch
 * @since 2023/7/4
 */
public class HashOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("sadd","srem","smembers","sismember","scard","smove",
                "spop","srandmember","sinter","sinterstore","sunion","sunionstore","sdiff","sdiffstore"));
    }

    @Override
    public String exec(CmdOpsContext ctx) {
        return null;
    }
}
