package com.dkl.flink.connector.http;

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(HttpClientUtil.post("http://mock.apifox.cn/m1/2518376-0-default/test/flink/post/order",""));
        System.out.println(HttpClientUtil.get("http://mock.apifox.cn/m1/2518376-0-default/test/flink/get/order"));
    }
}
