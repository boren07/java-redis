package com.borened.redis.util;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryBuilder {
    private String nameFormat;

    public ThreadFactoryBuilder setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public ThreadFactory build() {
        return new MyThreadFactory(nameFormat);
    }


    private static class MyThreadFactory implements ThreadFactory {
        private int threadNumber;
        private final String nameFormat;

        public MyThreadFactory(String nameFormat) {
            this.nameFormat = nameFormat;
        }

        @Override
        public Thread newThread(Runnable r) {
            threadNumber++;
            return new Thread(r, String.format(nameFormat, threadNumber));
        }
    }

}