package com.dkl.flink.hbase;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static com.dkl.flink.util.SecurityUtil.authenticateKerberos;

/**
 * Flink 本地读写远程服务器上带有 kerberos 认证的 HBase，便于本地调试代码。
 */
public class HbaseDemo {
    public static void main(String[] args){
        if (args.length < 3) {
            throw new RuntimeException("Usage: HbaseDemo <keytabPath> <principal> <krb5Path>");
        }

        String keytabPath = args[0];
        String principal = args[1];
        String krb5Path = args[2];

        // 认证 kerberos
        authenticateKerberos(keytabPath, principal, krb5Path);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, EnvironmentSettings.inBatchMode());

        String hbaseDDL = "CREATE TABLE flink_hbase_table(\n" +
                "id int,\n" +
                "cf ROW<name string,price double,ts bigint, dt string>,\n" +
                "PRIMARY KEY (id) NOT ENFORCED\n" +
                ") with (\n" +
                "  'connector' = 'hbase-2.2',\n" +
                "  'table-name' = 'flink_hbase_table',\n" +
                "  'zookeeper.quorum' = 'indata-192-168-44-128.indata.com:2181,indata-192-168-44-129.indata.com:2181,indata-192-168-44-130.indata.com:2181',\n" +
                "  'zookeeper.znode.parent' = '/hbase-secure',\n" +
                "  'properties.hbase.security.authentication' = 'kerberos',\n" +
                "  'properties.hbase.regionserver.kerberos.principal' = 'hbase/_HOST@INDATA.COM'\n" +
                ");";
        tableEnv.executeSql(hbaseDDL);

        Table hbaseQueryResult = tableEnv.sqlQuery("select id,name,price,ts,dt from flink_hbase_table");
        hbaseQueryResult.execute().print();
        tableEnv.executeSql("insert into flink_hbase_table values (2,ROW('hudi2',22.2,2000,'2023-12-13'))");
        hbaseQueryResult.execute().print();
    }
}
