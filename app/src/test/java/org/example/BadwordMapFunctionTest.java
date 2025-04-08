package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BadwordMapFunctionTest {
    public static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("fuck this damn project", List.of(
                        new BadwordEntry(List.of("fuck"), new Position(0, 3)),
                        new BadwordEntry(List.of("damn"), new Position(10, 13))
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testSplit(String record, List<BadwordEntry> entries) {
        assertEquals(entries, BadwordMapFunction.split(record));
    }
}
