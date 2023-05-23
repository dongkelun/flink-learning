package com.dkl.flink.connector.http;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DeserializationFormatFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.types.DataType;

import java.util.HashSet;
import java.util.Set;

public class HttpTableFactory implements DynamicTableSourceFactory {

    // 定义所有配置项
    // define all options statically
    public static final ConfigOption<String> URL = ConfigOptions.key("http.url")
            .stringType()
            .noDefaultValue();

    public static final ConfigOption<String> MODE = ConfigOptions.key("http.mode")
            .stringType()
            .defaultValue("get");

    public static final ConfigOption<Boolean> READ_AS_STREAMING = ConfigOptions
            .key("read.streaming.enabled")
            .booleanType()
            .defaultValue(false)// default read as batch
            .withDescription("Whether to read as streaming source, default false");

    public static final ConfigOption<Long> READ_STREAMING_CHECK_INTERVAL = ConfigOptions.key("read.streaming.check-interval")
            .longType()
            .defaultValue(60L)// default 1 minute
            .withDescription("Check interval for streaming read of SECOND, default 1 minute");


    @Override
    public String factoryIdentifier() {
        // 用于匹配： `connector = '...'`
        return "http"; // used for matching to `connector = '...'`
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(URL);
        // 解码的格式器使用预先定义的配置项
        options.add(FactoryUtil.FORMAT); // use pre-defined option for format
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(MODE);
        options.add(READ_AS_STREAMING);
        options.add(READ_STREAMING_CHECK_INTERVAL);
        return options;
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        // 使用提供的工具类或实现你自己的逻辑进行校验
        // either implement your custom validation logic here ...
        // or use the provided helper utility
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // 找到合适的解码器
        // discover a suitable decoding format
        final DecodingFormat<DeserializationSchema<RowData>> decodingFormat = helper.discoverDecodingFormat(
                DeserializationFormatFactory.class,
                FactoryUtil.FORMAT);

        // 校验所有的配置项
        // validate all options
        helper.validate();

        // 获取校验完的配置项
        // get the validated options
        final ReadableConfig options = helper.getOptions();
        final String url = options.get(URL);
        final String mode = options.get(MODE);
        final boolean isStreaming = options.get(READ_AS_STREAMING);
        final long interval = options.get(READ_STREAMING_CHECK_INTERVAL);

        // 从 catalog 中抽取要生产的数据类型 (除了需要计算的列)
        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType =
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();

        // 创建并返回动态表 source
        // create and return dynamic table source
        return new HttpTableSource(url, mode, isStreaming, interval, decodingFormat, producedDataType);
    }
}