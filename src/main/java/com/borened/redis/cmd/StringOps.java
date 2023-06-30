package com.borened.redis.cmd;

import com.borened.redis.RedisDb;
import com.borened.redis.util.StrUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author cch
 * @since 2023/6/30
 */
public class StringOps implements RedisOps {
    @Override
    public Set<String> support() {
        return new HashSet<>(Arrays.asList("set","setnx","setex","psetex","setrange",
                "mset","msetnx","append",
                "get","mget","getrange","getset","strlen",
                "decr","decrby","incr","incrby","incrbyfloat",
                "setbit","getbit","bitop","bitcount"
        ));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public String exec(CmdContext cmdContext) {
        RedisDb redisDb = cmdContext.getRedisDb();
        Map<String, Object> data = redisDb.getData();
        String cmd = cmdContext.getCmd();
        String[] args = cmdContext.getArgs();
        String result = null;
        switch (cmd) {
            case "set":
                data.put(args[0], args[1]);
                result =  StrUtil.OK;
                break;
            case "setnx":
                data.putIfAbsent(args[0], args[1]);
                result = String.valueOf(data.containsKey(args[0])? 0:1);
                break;
            case "setex":
                data.put(args[0], args[2]);
                result =  StrUtil.OK;
                //使用异步任务去删除
                CmdOpsExecutor.getInstance().getScheduledExecutorService().schedule(() -> {
                    data.remove(args[0]);
                }, Long.parseLong(args[1]), TimeUnit.SECONDS);
                break;
            case "get":
                result =  StrUtil.convert(data.get(args[0]));
                break;
            case "mget":
                List<Object> res = new ArrayList<>(args.length);
                for (String arg : args) {
                    res.add(data.get(arg));
                }
                result = StrUtil.pretty(res);
                break;
            default:
                break;
        }
        return result;
    }
}
