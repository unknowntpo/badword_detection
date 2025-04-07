package org.example;

import org.apache.flink.api.common.functions.MapFunction;

import java.util.List;

public class BadwordMapFunction implements MapFunction<String, List<BadwordEntry>> {

    @Override
    public List<BadwordEntry> map(String record) throws Exception {
        return List.of(new BadwordEntry(List.of("fuck"), new Position(0, 3)));
    }
}

