package org.example;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
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

        // configure your test environment
        env.setParallelism(2);

        // values are collected in a static variable
        CollectSink.values.clear();

        TypeInformation<List<BadwordEntry>> info = TypeInformation.of(new TypeHint<List<BadwordEntry>>() {
        });
        Map<String, Boolean> badWords = ImmutableMap.of("fuck", true, "shit", true, "damn", true);
        env.fromData("fuck this damn project")
                .map(s -> List.of(new BadwordEntry(List.of("fuck"), new BadwordEntry.Position(0, 3))))
                // need to add type information for lambda function
                .returns(info)
                .addSink(new CollectSink());

//        for each input data, expect output

//        e.g. "fuck this damn project"
//        [
//          {"keyword": ["damn", "fuck"] , position: {start: 0, end: 3} }
//          {"keyword": ["damn"] , position: {start: 10, end: 13} }
//        ]

        List<List<BadwordEntry>> values = CollectSink.values;

        env.execute();

        System.out.println(values);

        // verify your results
//        env.fromData("fuck", "world", "hello", "hello", "this", "is", "shit", "damn");
        // detect badword and offset
        //        e.g. "fuck this damn project"
//        [
//          {"keyword": ["fuck"] , position: {start: 0, end: 3} }
//          {"keyword": ["damn"] , position: {start: 10, end: 13} }
//        ]

        Assertions.assertTrue(CollectSink.values.containsAll(List.of(
                List.of(
                        new BadwordEntry(List.of("fuck"), new BadwordEntry.Position(0, 3)),
                        new BadwordEntry(List.of("damn"), new BadwordEntry.Position(10, 13))
                )
        )));
    }

    // create a testing sink
    private static class CollectSink implements SinkFunction<List<BadwordEntry>> {

        // must be static
        public static final List<List<BadwordEntry>> values = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void invoke(List<BadwordEntry> value, SinkFunction.Context context) throws Exception {
            values.add(value);
        }
    }

    public class BadwordEntry {
        //        [
//          {"keyword": ["damn", "fuck"] , position: {start: 0, end: 3} }
//          {"keyword": ["damn"] , position: {start: 10, end: 13} }
//        ]
        public List<String> keyword;
        public Position position;

        public BadwordEntry() {
        }

        public BadwordEntry(List<String> keyword, Position position) {
            this.keyword = keyword;
            this.position = position;
        }

        public static class Position {
            public int start;
            public int end;

            public Position() {}

            public Position(int start, int end) {
                this.start = start;
                this.end = end;
            }
        }
    }
}
