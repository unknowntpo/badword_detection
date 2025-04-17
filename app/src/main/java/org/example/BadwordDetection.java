package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.operators.collect.CollectSinkFunction;

/**
 * A flink job detecting badwords.
 */
public class BadwordDetection {
    private final StreamExecutionEnvironment env;
    private final Source<String, ?, ?> source;
    private final SinkFunction<BadwordEntries> sink;

    public BadwordDetection(Source<String, ?, ?> source, SinkFunction<BadwordEntries> sink) {
        this.source = source;
        this.sink = sink;

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // enable avro
        env.getConfig().enableForceAvro();
        env.getConfig().disableGenericTypes();
        System.out.println("enabled ? " + env.getConfig().isForceAvroEnabled());
        env.setParallelism(2);

        this.env = env;
    }
    // params: env (do this later), source, sink
    // test Badwordintegrationtest
    public void doJob() throws Exception {
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .map(new BadwordMapFunction())
                .addSink(sink);
        env.execute();
    }
}
