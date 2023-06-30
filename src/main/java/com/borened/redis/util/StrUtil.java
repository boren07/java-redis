package com.borened.redis.util;

import java.util.Collection;

/**
 * 字符串工具
 *
 * @author cch
 * @since 2023/6/30
 */
public class StrUtil {
    public static final String OK = "ok!";
    public static final String NIL = "nil";

    public static String convert(Object string){
        if ( string == null) {
            return NIL;
        }else {
            return string.toString();
        }
    }

    public static String pretty(Object source){
        StringBuilder builder = new StringBuilder();
        if (source instanceof String) {
            return builder.append("\n").append(source).toString();
        }else if (source instanceof Collection){
            int i = 1;
            for (Object item : ((Collection<?>) source)) {
                builder.append("\n").append(i).append(")").append(item);
                i++;
            }
            return builder.toString();
        }else {
            return builder.append("\n").append(source).toString();
        }

    }
}
