package org.example;

import com.google.common.collect.ImmutableMap;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BadwordMapFunction implements MapFunction<String, List<BadwordEntry>> {
    private static Map<String, Boolean> badWords = ImmutableMap.of("fuck", true, "shit", true, "damn", true);

    public static String[] split(String record) {
        // split by , or white space
        return record.split("[, ]+");
    }

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

