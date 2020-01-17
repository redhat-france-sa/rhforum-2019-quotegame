package com.redhat.quotegame.priceupdater.model;

import java.util.List;
import java.util.ArrayList;

import com.redhat.quotegame.model.Order;

import io.quarkus.runtime.annotations.RegisterForReflection;
/**
 * Wrapper type for a snasphot of list of orders.
 * @author laurent
 */
@RegisterForReflection
public class OrderListSnapshot {

    private Long timestamp;
    private List<Order> orders = new ArrayList<Order>();

    public OrderListSnapshot() {
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public Long getTimestamp() {
        return timestamp;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        if (!orders.contains(order)) {
            orders.add(order);
        }
    }
}