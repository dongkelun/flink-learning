package com.dkl.flink.connector.http;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.RowData;

import java.util.concurrent.TimeUnit;

/**
 * http table source
 */
public class HttpSourceFunction extends RichSourceFunction<RowData> {

    private volatile boolean isRunning = true;
    private String url;
    private String mode;
    private boolean isStreaming;
    private long interval;
    private DeserializationSchema<RowData> deserializer;
    // count out event
    private transient Counter counter;

    public HttpSourceFunction(String url, String mode, boolean isStreaming,
                              long interval, DeserializationSchema<RowData> deserializer) {
        this.url = url;
        this.mode = mode;
        this.isStreaming = isStreaming;
        this.interval = interval;
        this.deserializer = deserializer;
    }

    @Override
    public void open(Configuration parameters) {

        counter = new SimpleCounter();
        this.counter = getRuntimeContext()
                .getMetricGroup()
                .counter("http-counter");
    }

    @Override
    public void run(SourceContext<RowData> ctx) {
        if (isStreaming) {
            while (isRunning) {
                try {
                    // 接收http消息
                    // receive http message
                    String message = mode.equalsIgnoreCase("get") ? HttpClientUtil.get(url) : HttpClientUtil.post(url, "");
                    // 解码并处理记录
                    // deserializer message
                    ctx.collect(deserializer.deserialize(message.getBytes()));
                    this.counter.inc();

                    TimeUnit.SECONDS.sleep(interval);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                // receive http message
                String message = mode.equalsIgnoreCase("get") ? HttpClientUtil.get(url) : HttpClientUtil.post(url, "");
                // deserializer message
                ctx.collect(deserializer.deserialize(message.getBytes()));
                this.counter.inc();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
