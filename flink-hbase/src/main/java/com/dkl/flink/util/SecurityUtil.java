package com.dkl.flink.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class SecurityUtil {
    public static void authenticateKerberos(String keytabPath, String principal, String krb5Path){
        System.clearProperty("java.security.krb5.conf");
        System.setProperty("java.security.krb5.conf", krb5Path);
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
