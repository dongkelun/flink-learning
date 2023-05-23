package com.dkl.flink.connector.http;

public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
