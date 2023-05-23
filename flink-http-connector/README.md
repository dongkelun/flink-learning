# flink-http-connector

## 示例SQL
```sql
create table cust_http_post_source(
    id int,
    name string
)WITH(
 'connector' = 'http',
 'http.url' = 'http://mock.apifox.cn/m1/2518376-0-default/test/flink/post/order',
 'http.mode' = 'post',
 'http.interval' = '1000',
 'format' = 'json'
);

create table cust_http_get_source(
    id int,
    name string
)WITH(
 'connector' = 'http',
 'http.url' = 'http://mock.apifox.cn/m1/2518376-0-default/test/flink/get/order',
-- 'http.mode' = 'get',
-- 'http.interval' = '1000',
 'format' = 'json'
);
```

## Flink自定义Source官方文档
[dynamic-table-source](https://nightlies.apache.org/flink/flink-docs-release-1.15/zh/docs/dev/table/sourcessinks/#dynamic-table-source)