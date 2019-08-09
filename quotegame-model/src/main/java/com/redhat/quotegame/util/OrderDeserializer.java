package com.redhat.quotegame.util;

import com.redhat.quotegame.model.Order;
/**
 * Kafka deserializer for Order model.
 * @author laurent
 */
public class OrderDeserializer extends JsonPojoDeserializer<Order> {
    
    public OrderDeserializer() {
        this.clazz = Order.class;
    }
}