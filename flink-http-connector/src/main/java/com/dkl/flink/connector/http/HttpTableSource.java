package com.dkl.flink.connector.http;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

public class HttpTableSource implements ScanTableSource {

    private final String url;
    private final String mode;
    private final boolean isStreaming;
    private final long interval;
    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
    private final DataType producedDataType;

    public HttpTableSource(
            String hostname,
            String mode,
            boolean isStreaming,
            long interval,
            DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
            DataType producedDataType) {
        this.url = hostname;
        this.mode = mode;
        this.isStreaming = isStreaming;
        this.interval = interval;
        this.decodingFormat = decodingFormat;
        this.producedDataType = producedDataType;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        // 在我们的例子中，由解码器来决定 changelog 支持的模式
        // 但是在 source 端指定也可以
        // in our example the format decides about the changelog mode
        // but it could also be the source itself
        return decodingFormat.getChangelogMode();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {

        // 创建运行时类用于提交给集群
        // create runtime classes that are shipped to the cluster
        final DeserializationSchema<RowData> deserializer = decodingFormat.createRuntimeDecoder(
                runtimeProviderContext,
                producedDataType);

        final SourceFunction<RowData> sourceFunction = new HttpSourceFunction(url, mode, isStreaming, interval, deserializer);

        return SourceFunctionProvider.of(sourceFunction, !isStreaming);
    }

    @Override
    public DynamicTableSource copy() {
        return new HttpTableSource(url, mode, isStreaming, interval, decodingFormat, producedDataType);
    }

    @Override
    public String asSummaryString() {
        return "Http Table Source";
    }
}