package com.redhat.quotegame.priceupdater.model;

import com.redhat.quotegame.util.JsonPojoDeserializer;
/**
 * Kafka deserializer for OrderList model.
 * @author laurent
 */
public class OrderListSnapshotDeserializer extends JsonPojoDeserializer<OrderListSnapshot> {
    
    public OrderListSnapshotDeserializer() {
        this.clazz = OrderListSnapshot.class;
    }
}