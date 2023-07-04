package com.borened.redis.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * 字符串工具
 *
 * @author cch
 * @since 2023/6/30
 */
public class StrUtil {
    public static final String OK = "ok!";

    public static final String ZERO = "0";
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

    public static String oneOrZero(boolean flag){
        return flag ? "1" : "0";
    }

    public static String okMsg(){
        return OK;
    }
    public static String errorMsg(String message){
        if (message == null || message.length() == 0) {
            return "(error)";
        }
        return "(error) " + message;
    }



    public static  String getClientId() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            return hostAddress;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
