package testcontainers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class TestProducer {
    private final KafkaProducer<String, String> producer;
    private final String topic;

    public TestProducer(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Гарантируем доставку для теста
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        this.producer = new KafkaProducer<>(props);
    }

    public void sendEvent(String orderId, String jsonPayload) {
        // В качестве ключа Kafka используем orderId для обеспечения идемпотентности партиционирования
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, orderId, jsonPayload);
        try {
            producer.send(record).get();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка отправки в Kafka", e);
        }
    }

    public void close() {
        producer.close();
    }
}

