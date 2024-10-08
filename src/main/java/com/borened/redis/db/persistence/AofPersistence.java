package com.borened.redis.db.persistence;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.borened.redis.RedisDb.DB_ARR;

/**
 * @author chengcaihua
 * @description
 * @since 2024-08-02 16:24
 */
public class AofPersistence implements Persistence<ArrayList<List<String>>> {

    public static final String SELECT_DB_PREFIX = "select ";
    @Override
    public void store(ArrayList<List<String>> cmd, File file) throws IOException {
        try (BufferedWriter os = new BufferedWriter(new FileWriter(file))){
            int i =0;
            for (List<String> cmdLine : cmd){
                if (cmdLine.isEmpty()) {
                    continue;
                }
                os.write("select " + i);
                os.newLine();
                for (String cmdStr : cmdLine){
                    os.write(cmdStr);
                    os.newLine();
                }
                i++;
            }
        }

        rewriteOptimizeStorageSpace(file);
    }

    @Override
    public ArrayList<List<String>> load(File file) throws Exception {
        ArrayList<List<String>> aofCmds = new ArrayList<>(DB_ARR.length);
        try (BufferedReader is = new BufferedReader(new FileReader(file))){
            for (int i = 0; i < DB_ARR.length; i++) {
                aofCmds.add(new ArrayList<>());
            }
            String line;
            int dbIndex = 0;
            while ( (line = is.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (line.startsWith(SELECT_DB_PREFIX)) {
                    dbIndex = Integer.parseInt(line.substring(7));
                }else {
                    //todo 如果是到期命令，则需要重新计算到期时间
                    aofCmds.get(dbIndex).add(line);
                }
            }

        }

        return aofCmds;
    }
    public void rewriteOptimizeStorageSpace(File file){
        //todo 在适当的时机重写触发,优化aof文件大小
    }
}
