package com.borened.redis.cmd.ops;

import com.borened.redis.RedisDb;
import com.borened.redis.cmd.CmdOpsContext;
import com.borened.redis.util.RandomUtil;
import com.borened.redis.util.StrUtil;

import java.util.*;

/**
 * @author cch
 * @since 2023/7/4
 */
public class SetOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("sadd", "srem", "smembers", "sismember", "scard", "smove",
                "spop", "srandmember", "sinter", "sinterstore", "sunion", "sunionstore", "sdiff", "sdiffstore"));
    }

    @Override
    public String exec(CmdOpsContext cmdContext) {
        RedisDb redisDb = cmdContext.getRedisDb();
        Map<String, RedisDb.MetaData> data = redisDb.getData();
        String cmd = cmdContext.getCmd();
        String[] args = cmdContext.getArgs();
        String key = args[0];
        String result = null;
        RedisDb.MetaData metaData = data.get(key);
        HashSet<String> realData = null;
        switch (cmd) {
            case "sadd":
                if (metaData == null) {
                    realData = new HashSet<>();
                    metaData = new RedisDb.MetaData(realData);
                    data.put(key, metaData);
                } else {
                    realData = (HashSet<String>) metaData.getData();
                }
                for (int i = 1; i < args.length; i++) {
                    realData.add(args[i]);
                }
                result = String.valueOf(realData.size());
                break;
            case "srem":
                if (metaData == null) {
                    result = "0";
                    break;
                } else {
                    realData = (HashSet<String>) metaData.getData();
                }
                int delNum = 0;
                for (int i = 1; i < args.length; i++) {
                    boolean remove = realData.remove(args[i]);
                    if(remove){
                        delNum += delNum;
                    }
                }
                result = String.valueOf(delNum);
                break;
            case "smembers":
                if (metaData == null) {
                    result = StrUtil.NIL;
                } else {
                    result  = StrUtil.pretty(metaData.getData());
                }
                break;
            case "sismember":
                if (metaData == null) {
                    result = StrUtil.ZERO;
                    break;
                } else {
                    realData = (HashSet<String>) metaData.getData();
                    result = StrUtil.oneOrZero(realData.contains(args[1]));
                }
                break;
            case "scard":
                if (metaData == null) {
                    result = StrUtil.ZERO;
                    break;
                } else {
                    realData = (HashSet<String>) metaData.getData();
                    result = String.valueOf(realData.size());
                }
                break;
            case "smove":
                if (metaData == null) {
                    result = StrUtil.ZERO;
                    break;
                } else {
                    String dest = args[1];
                    String item = args[2];
                    realData = (HashSet<String>) metaData.getData();
                    if (!realData.contains(item)) {
                        result = StrUtil.ZERO;
                        break;
                    }else {
                        boolean remove = realData.remove(item);
                        result  = StrUtil.oneOrZero(remove);
                        HashSet<String> destSet = (HashSet<String>) data.get(dest).getData();
                        destSet.add(item);
                    }
                }
                break;
            case "spop":
                if (metaData == null) {
                    result = StrUtil.NIL;
                    break;
                } else {
                    realData = (HashSet<String>) metaData.getData();
                    int setIndex = new Random().nextInt(realData.size());
                    String remove = null;
                    int i = 0;
                    for (String realDatum : realData) {
                        if (i==setIndex) {
                            remove = realDatum;
                            break;
                        }
                        i++;
                    }
                    realData.remove(remove);
                    result = remove;
                }
                break;
            case "srandmember":
                if (metaData == null) {
                    result = StrUtil.NIL;
                    break;
                } else {
                    realData = (HashSet<String>) metaData.getData();
                    int count = args.length>1 ? Integer.parseInt(args[1]) : 1;
                    Integer[] randomArray = RandomUtil.randomArray(count, realData.size());
                    List<String> removes = new ArrayList<>(count);
                    int i = 0;
                    for (String realDatum : realData) {
                        if (Arrays.asList(randomArray).contains(i)){
                            removes.add(realDatum);
                        }
                        i++;
                    }
                    result = StrUtil.pretty(removes);
                }
                break;
            default:
                break;
        }
        return result;
    }
}
