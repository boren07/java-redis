package com.borened.redis.cmd;

import java.util.Collections;
import java.util.Set;

/**
 * @author cch
 * @since 2023/6/30
 */
public class ListOps implements RedisOps {
    @Override
    public Set<String> support() {
        return Collections.emptySet();
    }

    @Override
    public String exec(CmdContext cmdContext) {
        return null;
    }
}
