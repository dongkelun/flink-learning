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

public class HttpDynamicTableSource implements ScanTableSource {

    private final String url;
    private final String mode;
    private final long interval;
    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
    private final DataType producedDataType;

    public HttpDynamicTableSource(
            String hostname,
            String mode,
            long interval,
            DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
            DataType producedDataType) {
        this.url = hostname;
        this.mode = mode;
        this.interval = interval;
        this.decodingFormat = decodingFormat;
        this.producedDataType = producedDataType;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        // in our example the format decides about the changelog mode
        // but it could also be the source itself
        return decodingFormat.getChangelogMode();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {

        // create runtime classes that are shipped to the cluster
        final DeserializationSchema<RowData> deserializer = decodingFormat.createRuntimeDecoder(
                runtimeProviderContext,
                producedDataType);

        final SourceFunction<RowData> sourceFunction = new HttpSourceFunction(url, mode, interval, deserializer);

        return SourceFunctionProvider.of(sourceFunction, true);
    }

    @Override
    public DynamicTableSource copy() {
        return new HttpDynamicTableSource(url, mode, interval, decodingFormat, producedDataType);
    }

    @Override
    public String asSummaryString() {
        return "Http Table Source";
    }
}