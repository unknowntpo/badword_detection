package org.example;

import org.apache.flink.api.common.functions.MapFunction;

import java.util.ArrayList;
import java.util.List;

public class BadwordMapFunction implements MapFunction<String, List<BadwordEntry>> {

    @Override
    public List<BadwordEntry> map(String record) throws Exception {
        List<BadwordEntry> entries = new ArrayList<>();
        entries.add(new BadwordEntry(
                new ArrayList<>(List.of("fuck")),  // Use ArrayList instead of List.of()
                new Position(0, 3)
        ));
        entries.add(new BadwordEntry(
                new ArrayList<>(List.of("damn")),
                new Position(10, 13)
        ));
        return entries;
    }
}

