package com.borened.redis.cmd.ops;

import com.borened.redis.RedisInfo;
import com.borened.redis.RedisServer;
import com.borened.redis.cmd.CmdOpsContext;
import com.borened.redis.util.SingletonFactory;
import com.borened.redis.util.StrUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cch
 * @since 2023/7/4
 */
public class ConnectionOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("select","echo","quit","info"));
    }

    @Override
    public String exec(CmdOpsContext ctx) {
        String cmd = ctx.getCmd();
        String[] args = ctx.getArgs();
        String result = StrUtil.OK;
        if ("select".equals(cmd)) {
            CmdOpsContext.clientSession.put(StrUtil.getClientId(), RedisServer.getDb(Integer.parseInt(args[0])));
            result = "success switch database " + args[0];
        }
        else if ("echo".equals(cmd)) {
            result = args[1];
        } else if ("quit".equals(cmd)) {
            Runtime.getRuntime().exit(0);
        }else if ("info".equals(cmd)) {
            result = SingletonFactory.getSingleton(RedisInfo.class).toString();
        }
        return result;
    }
}
