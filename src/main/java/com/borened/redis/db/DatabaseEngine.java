package com.borened.redis.db;

import com.borened.redis.RedisDb;
import com.borened.redis.RedisException;
import com.borened.redis.RedisInfo;
import com.borened.redis.cmd.CmdOpsExecutor;
import com.borened.redis.config.ConfigManager;
import com.borened.redis.config.ConfigProperties;
import com.borened.redis.consts.Constants;
import com.borened.redis.db.persistence.AofPersistence;
import com.borened.redis.db.persistence.RdbPersistence;
import com.borened.redis.event.KeyChangeEvent;
import com.borened.redis.observer.KeyObservable;
import com.borened.redis.util.SingletonFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.borened.redis.RedisDb.DB_ARR;

/**
 * @author chengcaihua
 * @description 数据库管理器
 * @since 2024-08-02 16:23
 */
public class DatabaseEngine implements Observer {


    public static final RdbPersistence rdbPersistence = new RdbPersistence();
    public static final AofPersistence aofPersistence = new AofPersistence();

    public static final String PERSISTENCE_MODE;
    public static final File RDB_DATA_DIR = new File("/db" + File.separator + "dump.rdb");
    public static final File AOF_DATA_DIR = new File("/db" + File.separator + "dump.aof");

    public ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    public ArrayList<List<String>> aofCacheCommands= new ArrayList<>(DB_ARR.length);

    static {
        ConfigProperties configProperties = ConfigManager.getConfigProperties();
        PERSISTENCE_MODE = configProperties.getPersistenceMode();
    }

    public DatabaseEngine(){
        System.out.println("DatabaseEngine init...");
        init();
    }
    public void init() {
        initDataDir();
        recoverDataFromDisk();
        // init database space when need
        initMetaDataSpaceNeed();
        if (Constants.RDB.equals(PERSISTENCE_MODE)) {
            startScheduleRdbSave();
        } else if (Constants.AOF.equals(PERSISTENCE_MODE)) {
            startScheduleAofSave();
            SingletonFactory.getSingleton(KeyObservable.class).addObserver(this);
            aofPersistence.rewriteOptimizeStorageSpace(AOF_DATA_DIR);
        }

    }

    /**
     * 初始化数据目录
     */
    private void initDataDir() {
        if (!RDB_DATA_DIR.exists()){
            RDB_DATA_DIR.getParentFile().mkdirs();
            try {
                RDB_DATA_DIR.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!AOF_DATA_DIR.exists()){
            AOF_DATA_DIR.getParentFile().mkdirs();
            try {
                AOF_DATA_DIR.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < DB_ARR.length; i++) {
            aofCacheCommands.add(new ArrayList<>());
        }
    }

    /*
     * 初始化数据库空间
     */
    private void initMetaDataSpaceNeed() {
        boolean init = SingletonFactory.exist(RedisInfo.class);
        if (init) {
            return;
        }
        //init database space
        List<RedisDb> redisDbs = new ArrayList<>(RedisDb.DB_ARR.length);
        for (int index : DB_ARR) {
            redisDbs.add(new RedisDb(index));
        }
        RedisInfo redisInfo = new RedisInfo();
        redisInfo.setRedisDbs(redisDbs);
        SingletonFactory.registerSingleton(redisInfo);
    }

    /**
     * 从磁盘恢复数据
     * <p>
     *     <ul>
     *         <li>rdb 通过序列化恢复</li>
     *         <li>aof 通过aof命令重放来恢复</li>
     *     </ul>
     * </p>
     */
    private void recoverDataFromDisk() {
        //rdb 通过序列化恢复
        if (Constants.RDB.equals(PERSISTENCE_MODE)) {
            RedisInfo load = null;
            try {
                load = rdbPersistence.load(RDB_DATA_DIR);
            } catch (Exception e) {
                throw new RedisException("load rdb file error",e);
            }

            if (load != null){
                SingletonFactory.registerSingleton(load);
            }
        }else if (Constants.AOF.equals(PERSISTENCE_MODE)) {
            //aof 重放恢复
            ArrayList<List<String>> arr = null;
            try {
                arr = aofPersistence.load(AOF_DATA_DIR);
            } catch (Exception e) {
                throw new RedisException("load aof file error",e);
            }
            //初始化存储
            initMetaDataSpaceNeed();
            //命令重放
            for (int i = 0; i < arr.size(); i++) {
                for (String cmd : arr.get(i)) {
                    CmdOpsExecutor.getInstance().innerExecute(cmd,i);
                }
            }

        }

    }


    public void startScheduleRdbSave() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            RedisInfo redisInfo = SingletonFactory.getSingleton(RedisInfo.class);
            try {
                rdbPersistence.store(redisInfo, RDB_DATA_DIR);
            } catch (IOException e) {
                System.err.println("rdb save error...");
                e.printStackTrace();
            }
        },0,10, TimeUnit.SECONDS);
    }

    private void startScheduleAofSave() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                aofPersistence.store(aofCacheCommands, AOF_DATA_DIR);
                aofCacheCommands.forEach(List::clear);
            } catch (IOException e) {
                System.err.println("aof save error...");
                e.printStackTrace();
            }
        },0,10, TimeUnit.SECONDS);
    }
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof KeyChangeEvent) {
            KeyChangeEvent changeEvent = (KeyChangeEvent) arg;
            String cmd = changeEvent.getCmd();

            aofCacheCommands.get(changeEvent.getRedisDb().getIndex()).add(cmd);
        }
    }
}
