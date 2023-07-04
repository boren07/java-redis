package com.borened.redis.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * 随机数工具
 *
 * @author cch
 * @since 2023/7/4
 */
public class RandomUtil {


    public static Random random() {
        return new Random();
    }


    public static Integer[] randomArray(int length,int bounds) {
        if (bounds<length) {
            bounds = length;
        }
        Integer[] ints = new Integer[length];
        int offset = 0;
        do {
            int anInt = random().nextInt(bounds);
            boolean repeat = Arrays.stream(ints).filter(Objects::nonNull).anyMatch(a -> a == anInt);
            System.out.println("生成随机数："+anInt);
            if (!repeat) {
                ints[offset] = anInt;
                offset++;
            }
        } while (offset<length);
        return ints;
    }
}
