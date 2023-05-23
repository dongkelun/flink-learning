# flink-http-connector

## 示例SQL
```sql
create table cust_http_get_source(
    id int,
    name string
)WITH(
 'connector' = 'http',
 'http.url' = 'http://mock.apifox.cn/m1/2518376-0-default/test/flink/get/order',
 'format' = 'json'
);

create table cust_http_post_source(
    id int,
    name string
)WITH(
 'connector' = 'http',
 'http.url' = 'http://mock.apifox.cn/m1/2518376-0-default/test/flink/post/order',
 'http.mode' = 'post',
 'read.streaming.enabled' = 'true',
 'read.streaming.check-interval' = '10',
 'format' = 'json'
);

```

## Flink自定义Source官方文档
[Flink 1.15 英文](https://nightlies.apache.org/flink/flink-docs-release-1.15/zh/docs/dev/table/sourcessinks)
[Flink 1.17 中文](https://nightlies.apache.org/flink/flink-docs-release-1.17/zh/docs/dev/table/sourcessinks)