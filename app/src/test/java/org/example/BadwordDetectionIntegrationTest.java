package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.apache.flink.connector.datagen.functions.*;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class BadwordDetectionIntegrationTest {

    private static final String TOPIC_NAME = "chat_messages";
    @ClassRule
    public static MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(2).setNumberTaskManagers(1).build());
    private static AdminClient adminClient;
    private static NewTopic topic;
    private static String bootstrapServers = "localhost:9092";
    private static KafkaProducer producer;

    @BeforeAll
    public static void beforeAll() {
        // setup kafka

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);

        adminClient = AdminClient.create(props);

    }

    @BeforeEach
    public void beforeEach() {
        createOrResetTopic();
        createProducer();
    }

    public static void createProducer() {
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", bootstrapServers);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("acks", "all");

        BadwordDetectionIntegrationTest.producer = new KafkaProducer(producerProps);
    }

    private static void createOrResetTopic() {
        try {
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singletonList(TOPIC_NAME));
            result.all().get();
        } catch (Exception e) {
        }

        NewTopic topic = new NewTopic(TOPIC_NAME, 1, (short) 1);
        adminClient.createTopics(Collections.singletonList(topic));
        BadwordDetectionIntegrationTest.topic = topic;
    }

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

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(TOPIC_NAME)
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .map(new BadwordMapFunction())
                .addSink(new CollectSink());

//        env.fromData("fuck this damn project")
//                .map(new BadwordMapFunction())
//                .addSink(new CollectSink());

        // send some message
        for (int i = 0; i < 30; i++) {
            producer.send(new ProducerRecord<String, String>(TOPIC_NAME, "msg_partition_1", "fuck this damn project" + String.valueOf(i))).get();
        }

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
