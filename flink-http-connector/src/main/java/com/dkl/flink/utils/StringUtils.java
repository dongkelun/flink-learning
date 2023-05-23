package com.dkl.flink.utils;

public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
