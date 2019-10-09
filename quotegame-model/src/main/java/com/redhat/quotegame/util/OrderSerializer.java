package com.redhat.quotegame.util;

import com.redhat.quotegame.model.Order;
/**
 * Kafka serializer for Order model.
 * @author laurent
 */
public class OrderSerializer extends JsonPojoSerializer<Order> {
    
    public OrderSerializer() {
    }
}