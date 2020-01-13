package com.redhat.quotegame.priceupdater;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.redhat.quotegame.priceupdater.model.OrderListSnapshot;
import com.redhat.quotegame.priceupdater.model.OrderListSnapshotSerializer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KafkaOrderListSnapshotProducerManager {

    private Producer<Long, OrderListSnapshot> producer;

    @ConfigProperty(name = "kafka.bootstrap-service")
    String bootstrapService;

    @ConfigProperty(name = "kafka.workingmemory-snapshots-out.topic")
    String snapshotsTopic;

    @PostConstruct
    public void create() {
        producer = createProducer();
    }

    protected Producer<Long, OrderListSnapshot> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapService);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "quotegame-priceupdater");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderListSnapshotSerializer.class.getName());
        return new KafkaProducer<Long, OrderListSnapshot>(props);
    }

    public void publish(OrderListSnapshot orderList) {
        producer.send(new ProducerRecord<>(snapshotsTopic, orderList.getTimestamp(), orderList));
        producer.flush();
    }
}