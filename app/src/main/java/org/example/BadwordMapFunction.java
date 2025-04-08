package org.example;

import com.google.common.collect.ImmutableMap;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadwordMapFunction implements MapFunction<String, List<BadwordEntry>> {
    private static Map<String, Boolean> badWords = ImmutableMap.of("fuck", true, "shit", true, "damn", true);

//    public static String[] split(String record) {
//        // split by , or white space
//        return record.split("[, ]+");
//    }

    public static List<BadwordEntry> split(String record) {
        ArrayList<BadwordEntry> entries = new ArrayList<BadwordEntry>();
        Map<String, Boolean> del = Map.of(",", true, " ", true);
        int len = record.length();
        int l = 0;
        int r = l;
        char[] chars = record.toCharArray();
        while (l < len) {
//            r = l;
            // r == l
            while (r < len && !del.containsKey(String.valueOf(chars[r]))) {
                r++;
            }
            // word: [l, r]
            String word = record.substring(l, r);
            System.out.println("word: " + word);
            if (badWords.containsKey(word)) {
                entries.add(new BadwordEntry(List.of(word), new Position(l, r - 1)));
            }
            // skip delimiters
            while (r < len && del.containsKey(String.valueOf(chars[r]))) {
                r++;
            }
            l = r;
        }

        return entries;
    }

    @Override
    public List<BadwordEntry> map(String record) throws Exception {
//        List<BadwordEntry> entries = new ArrayList<>();
        return split(record);
//        Arrays.stream(split(record)).forEach(word -> {
//            if (badWords.containsKey(word)) {
//                entries.add(new BadwordEntry(
//                        new ArrayList<>(List.of(word)),  // Use ArrayList instead of List.of()
//                        new Position(0, 3)
//                ));
//            };
//        });
//        return entries;
//
//        entries.add(new BadwordEntry(
//                new ArrayList<>(List.of("fuck")),  // Use ArrayList instead of List.of()
//                new Position(0, 3)
//        ));
//        entries.add(new BadwordEntry(
//                new ArrayList<>(List.of("damn")),
//                new Position(10, 13)
//        ));
//        return entries;
    }
}

