package com.redhat.quotegame.priceupdater.model;

import com.redhat.quotegame.util.JsonPojoSerializer;
/**
 * Kafka serializer for OrderListSnapshot model.
 * @author laurent
 */
public class OrderListSnapshotSerializer extends JsonPojoSerializer<OrderListSnapshot> {
    
    public OrderListSnapshotSerializer() {
    }
}