package com.borened.redis.cmd.ops;

import com.borened.redis.RedisDb;
import com.borened.redis.cmd.CmdOpsContext;
import com.borened.redis.consts.Constants;
import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.util.StrUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共操作
 * @author cch
 * @since 2023/6/30
 */
public class CommonOps implements RedisOps {
    @Override
    public Set<String> supports() {
        return new HashSet<>(Arrays.asList("del","keys","randomkey","ttl","pttl",
                "exists","move","rename",
                "renamenx","type","expire","pexpire","expireat",
                "pexpireat","persist","sort","object","migrate",
                "dump","restore"
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
            case "del":
                int delNum = 0;
                for (String key : args) {
                    RedisDb.MetaData remove = data.remove(key);
                    if (remove.getData() !=null){
                        delNum += 1;
                        cmdContext.getKeyObservable().notifyObservers(KeyChangeEvent.delOf(redisDb,key,remove));
                    }
                }
                result = String.valueOf(delNum);
                break;
            case "keys":
                String pattern = args[0];
                if (pattern.contains("*")) {
                    pattern = pattern.replace("*","(\\w*)");
                }
                else if (pattern.contains("?")) {
                    pattern = pattern.replace("?","(\\w)");
                }
/*                else if (pattern.matches(".*\\[\\w*\\].*")) {
                    pattern = pattern.replace("?","(\\w)");
                }*/
                Pattern compile = Pattern.compile("^("+ pattern+")$");
                Set<String> res = new LinkedHashSet<>();
                for (String key : data.keySet()) {
                    Matcher matcher = compile.matcher(key);
                    if (matcher.matches() && matcher.find(0)) {
                        res.add(key);
                    }
                }
                result = StrUtil.pretty(res);
                break;
            case "randomkey":
                if (data.isEmpty()) {
                    result = StrUtil.NIL;
                }else {
                    List<String> set = new ArrayList<>(data.keySet());
                    result = set.get(new Random().nextInt(data.size()));
                }
                break;
            case "ttl":
                RedisDb.MetaData metaData = data.get(args[0]);
                if (metaData == null) {
                    result = "-2";
                }else {
                    if (metaData.getExpireAt()  == Constants.NEVER_EXPIRES) {
                        result = String.valueOf(Constants.NEVER_EXPIRES);
                    }else {
                        result =  StrUtil.convert((metaData.getExpireAt()-System.currentTimeMillis())/1000);
                    }
                }
                break;
            case "pttl":
                RedisDb.MetaData metaData1 = data.get(args[0]);
                if (metaData1 == null) {
                    result = "-2";
                }else {
                    result =  StrUtil.convert(metaData1.getExpireAt()-System.currentTimeMillis());
                }
                break;
            case "exists":
                result = StrUtil.oneOrZero(data.containsKey(args[0]));
                break;
            case "rename":
                if (!data.containsKey(args[0])) {
                    result  = StrUtil.errorMsg("no such key");
                }else {
                    data.put(args[1],data.get(args[0]));
                    data.remove(args[0]);
                    result = StrUtil.OK;
                }
                break;
            case "type":
                if (!data.containsKey(args[0])) {
                    result  = StrUtil.errorMsg("no such key");
                }else {
                    RedisDb.MetaData metaData2 = data.get(args[0]);
                    result = metaData2.getData().getClass().toString();
                }
                break;
            case "expire":
                if (!data.containsKey(args[0])) {
                    result  = StrUtil.errorMsg("no such key");
                }else {
                    redisDb.registerKeyExpireEvent(args[0], Long.parseLong(args[1]),Long.parseLong(args[2]));
                    result = StrUtil.oneOrZero(true);
                }
                break;
            default:
                break;
        }
        return result;
    }

}
