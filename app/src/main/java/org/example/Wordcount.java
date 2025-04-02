package org.example;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Wordcount {
    public static void main(String[] args) throws Exception {
        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment()) {

            // configure your test environment
            env.setParallelism(2);

            // create a stream of custom elements and apply transformations
            TypeInformation<Tuple2<String, Integer>> info = TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {
            });
            SingleOutputStreamOperator<Tuple2<String, Integer>> ds = env.fromData("hello", "world", "hello", "hello", "this", "is")
                    .map(s -> new Tuple2<String, Integer>(s, 1))
                    // need to add type information for lambda function
                    .returns(info)
                    .keyBy(t -> {
                        return t.f0;
                    })
                    .sum(1)
                    .returns(info);

            ds.print();

            // execute
            env.execute("Wordcount example");
        }
    }
}
