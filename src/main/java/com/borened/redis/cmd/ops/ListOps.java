package com.borened.redis.cmd.ops;

import com.borened.redis.RedisDb;
import com.borened.redis.cmd.CmdOpsContext;
import com.borened.redis.util.StrUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cch
 * @since 2023/6/30
 */
public class ListOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("lpush","lpushx","rpush","rpushx",
                "lpop", "rpop",
                "llen","lrange", "lrem"

        ));
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
        LinkedList<String> realData = null;
        switch(cmd) {
            case "lpush":
                if (metaData ==null) {
                    realData = new LinkedList<>();
                    metaData = new RedisDb.MetaData(realData);
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                }
                for (int i = 1; i < args.length; i++) {
                    realData.addFirst(args[i]);
                }
                data.put(key,metaData);
                result = String.valueOf(realData.size());
                break;
            case "lpushx":
                if (metaData ==null) {
                    result = "0";
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                }
                for (int i = 1; i < args.length; i++) {
                    realData.addFirst(args[i]);
                }
                data.put(key,metaData);
                result = String.valueOf(realData.size());
                break;
            case "rpush":
                if (metaData ==null) {
                    realData = new LinkedList<>();
                    metaData = new RedisDb.MetaData(realData);
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                }
                for (int i = 1; i < args.length; i++) {
                    realData.addLast(args[i]);
                }
                data.put(key,metaData);
                result = String.valueOf(realData.size());
                break;
            case "rpushx":
                if (metaData ==null) {
                    result = StrUtil.ZERO;
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                }
                for (int i = 1; i < args.length; i++) {
                    realData.addLast(args[i]);
                }
                data.put(key,metaData);
                result = String.valueOf(realData.size());
                break;
            case "lpop":
                if (metaData ==null) {
                    result = StrUtil.NIL;
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                    result = realData.pollFirst();
                }
                break;
            case "rpop":
                if (metaData ==null) {
                    result = StrUtil.NIL;
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                    result = realData.pollLast();
                }
                break;
            case "llen":
                if (metaData ==null) {
                    result = StrUtil.ZERO;
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                    result = String.valueOf(realData.size());
                }
                break;
            case "lrange":
                if (metaData ==null) {
                    result = StrUtil.NIL;
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                    String start = args[1];
                    String stop = args[2];
                    List<String> collect = realData.stream().skip(Long.parseLong(start)).limit(Long.parseLong(stop)).collect(Collectors.toList());
                    result = StrUtil.pretty(collect);
                }
                break;
            case "lrem":
                if (metaData ==null) {
                    result = StrUtil.ZERO;
                    break;
                }else {
                    realData = (LinkedList<String>) metaData.getData();
                    Integer count = Integer.parseInt(args[1]);
                    String item = args[2];
                    String last;
                    int delItemNum = 0;
                    if (count>0){
                        while ((last = realData.peekLast()) !=null) {
                            if (delItemNum == count) {
                                break;
                            }
                            if (last.equals(item)) {
                                realData.removeLast();
                                delItemNum++;
                            }
                        }
                    } else if (count < 0) {
                        while ((last = realData.peekFirst()) !=null) {
                            if (delItemNum == count) {
                                break;
                            }
                            if (last.equals(item)) {
                                realData.removeFirst();
                                delItemNum++;
                            }
                        }
                    }

                    result = String.valueOf(delItemNum);
                }
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public RedisDataType type() {
        return RedisDataType.LIST;
    }
}
