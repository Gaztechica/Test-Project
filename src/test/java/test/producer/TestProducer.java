package test.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class TestProducer implements AutoCloseable {
    private final KafkaProducer<String, String> producer;

    public TestProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    public void sendOrderEvent(String topic, String orderId, String jsonPayload) throws Exception {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, orderId, jsonPayload);
        producer.send(record).get();
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}