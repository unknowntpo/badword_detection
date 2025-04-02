package org.example;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class WordcountIntegrationTest {

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(2).setNumberTaskManagers(1).build());

    @Test
    public void testWordcount() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // configure your test environment
        env.setParallelism(2);

        // values are collected in a static variable
        CollectSink.values.clear();

        // create a stream of custom elements and apply transformations
        TypeInformation<Tuple2<String, Integer>> info = TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {
        });
        env.fromData("hello", "world", "hello", "hello", "this", "is")
                .map(s -> new Tuple2<String, Integer>(s, 1))
                // need to add type information for lambda function
                .returns(info)
                .keyBy(t -> {
                    return t.f0;
                })
                .sum(1)
                .returns(info)
                .addSink(new CollectSink());

//         env.fromData("hello", "world", "hello", "hello", "this", "is")
//                .map(s -> new Tuple2<String, Integer>(s, 1))
//                .addSink(new CollectSink());
//                .keyBy(t -> {
//                    return t.f0;
//                })
//                .sum(1)
//                .returns(info)
//                .addSink(new CollectSink());
        List<Tuple2<String, Integer>> values = CollectSink.values;

//        Types.TUPLE(Types.STRING, Types.INT);

        env.execute();

        System.out.println(values);

        // verify your results
        Assertions.assertTrue(CollectSink.values.containsAll(List.of(
                new Tuple2("hello", 3),
                new Tuple2("world", 1),
                new Tuple2("this", 1),
                new Tuple2("is", 1))));
    }

    // create a testing sink
    private static class CollectSink implements SinkFunction<Tuple2<String, Integer>> {

        // must be static
        public static final List<Tuple2<String, Integer>> values = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void invoke(Tuple2<String, Integer> value, SinkFunction.Context context) throws Exception {
            values.add(value);
        }
    }
}
