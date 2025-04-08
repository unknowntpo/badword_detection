package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BadwordMapFunctionTest {
    public static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("hello world", new String[]{"hello", "world"}),
                Arguments.of("hello,world", new String[]{"hello", "world"}),
                Arguments.of("hello , world", new String[]{"hello", "world"}),
                Arguments.of("hello ,  world", new String[]{"hello", "world"})
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    public void testSplit(String record, String[] words) {
        BadwordMapFunction fn = new BadwordMapFunction();
        assertArrayEquals(words, BadwordMapFunction.split(record));
    }
}
