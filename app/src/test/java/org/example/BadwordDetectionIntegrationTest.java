package org.example;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.avro.AvroInputFormat;
import org.apache.flink.formats.avro.typeutils.AvroSerializer;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class BadwordDetectionIntegrationTest {

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(2).setNumberTaskManagers(1).build());

    @Test
    public void testBadwordDetection() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // enable avro
        env.getConfig().enableForceAvro();
        env.getConfig().disableGenericTypes();

        System.out.println("enabled ? " + env.getConfig().isForceAvroEnabled());

        // configure your test environment
        env.setParallelism(2);

        // values are collected in a static variable
        CollectSink.values.clear();

        env.fromData("fuck this damn project")
                .map(new BadwordMapFunction())
                .addSink(new CollectSink());


        List<BadwordEntries> values = CollectSink.values;

        env.execute();

        System.out.println(CollectSink.values);

        Assertions.assertTrue(CollectSink.values.contains(
                new BadwordEntries(List.of(new BadwordEntry(List.of("fuck"), new Position(0, 3)), new BadwordEntry(List.of("damn"), new Position(10, 13))))));
    }

    // create a testing sink
    private static class CollectSink implements SinkFunction<BadwordEntries> {

        // must be static
        public static final List<BadwordEntries> values = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void invoke(BadwordEntries value, SinkFunction.Context context) throws Exception {
            values.add(value);
        }
    }
}
