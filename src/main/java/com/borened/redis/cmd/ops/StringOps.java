package com.borened.redis.cmd.ops;

import com.borened.redis.RedisDb;
import com.borened.redis.cmd.CmdOpsContext;
import com.borened.redis.util.StrUtil;

import java.util.*;

/**
 * @author cch
 * @since 2023/6/30
 */
@SuppressWarnings("SpellCheckingInspection")
public class StringOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("set","setnx","setex","psetex","setrange",
                "mset","msetnx","append",
                "get","mget","getrange","getset","strlen",
                "decr","decrby","incr","incrby","incrbyfloat",
                "setbit","getbit","bitop","bitcount"
        ));
    }

    @Override
    public String exec(CmdOpsContext cmdContext) {
        RedisDb redisDb = cmdContext.getRedisDb();
        Map<String, RedisDb.MetaData> data = redisDb.getData();
        String cmd = cmdContext.getCmd();
        String[] args = cmdContext.getArgs();
        String result = null;
        switch (cmd) {
            case "set":
                RedisDb.MetaData oldVal0 = data.put(args[0], new RedisDb.MetaData(args[1]));
                result =  StrUtil.OK;
                notifyKeyObservers(cmdContext, args[0], oldVal0,args[1]);
                break;
            case "setnx":
                String key1 = args[0];
                result = String.valueOf(data.containsKey(key1)? 0:1);
                RedisDb.MetaData oldVal1 = data.putIfAbsent(key1, new RedisDb.MetaData(args[1]));
                notifyKeyObservers(cmdContext,key1,oldVal1,args[1]);
                break;
            case "setex":
                String key = args[0];
                long seconds = Long.parseLong(args[1]);
                String value = args[2];
                RedisDb.MetaData oldVal2 = data.put(key, new RedisDb.MetaData(value));
                redisDb.registerKeyExpireEvent(key,seconds,Long.parseLong(args[3]));
                result =  StrUtil.OK;

                notifyKeyObservers(cmdContext, key, oldVal2,value);
                break;
            case "get":
                result =  Optional.ofNullable(data.get(args[0])).map(md-> StrUtil.convert(md.getData())).orElse(StrUtil.NIL);
                break;
            case "mget":
                List<Object> res = new ArrayList<>(args.length);
                for (String arg : args) {
                    res.add(Optional.ofNullable(data.get(args[0])).map(md-> StrUtil.convert(md.getData())).orElse(StrUtil.NIL));
                }
                result = StrUtil.pretty(res);
                break;
            default:
                break;
        }
        return result;
    }


    @Override
    public RedisDataType type() {
        return RedisDataType.STRING;
    }
}
