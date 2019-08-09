package com.redhat.quotegame.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
/**
 * Generic Kafka deserialiser for reading JSON to POJO. 
 * @param <T>
 * @author laurent
 */
public class JsonPojoDeserializer<T> implements Deserializer<T> {

    protected final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    protected Class<T> clazz;

    @Override
    public void configure(Map<String, ?> props, boolean data) { }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        T data;
        try {
            data = OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
        return data;
    }

    @Override
    public void close() { }
}