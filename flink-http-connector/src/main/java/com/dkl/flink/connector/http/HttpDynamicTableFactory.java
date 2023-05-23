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

public class HttpDynamicTableFactory implements DynamicTableSourceFactory {

    // define all options statically
    public static final ConfigOption<String> URL = ConfigOptions.key("http.url")
            .stringType()
            .noDefaultValue();

    public static final ConfigOption<String> MODE = ConfigOptions.key("http.mode")
            .stringType()
            .defaultValue("get");

    public static final ConfigOption<Long> INTERVAL = ConfigOptions.key("http.interval")
            .longType()
            .defaultValue(60L)// default 1 minute
            .withDescription("Check interval for streaming read of SECOND, default 1 minute");


    @Override
    public String factoryIdentifier() {
        return "http"; // used for matching to `connector = '...'`
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(URL);
        options.add(FactoryUtil.FORMAT); // use pre-defined option for format
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(MODE);
        options.add(INTERVAL);
        return options;
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        // either implement your custom validation logic here ...
        // or use the provided helper utility
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
        final DecodingFormat<DeserializationSchema<RowData>> decodingFormat = helper.discoverDecodingFormat(
                DeserializationFormatFactory.class,
                FactoryUtil.FORMAT);

        // validate all options
        helper.validate();

        // get the validated options
        final ReadableConfig options = helper.getOptions();
        final String url = options.get(URL);
        final String mode = options.get(MODE);
        final long interval = options.get(INTERVAL);

        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType =
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();

        // create and return dynamic table source
        return new HttpDynamicTableSource(url, mode, interval, decodingFormat, producedDataType);
    }
}