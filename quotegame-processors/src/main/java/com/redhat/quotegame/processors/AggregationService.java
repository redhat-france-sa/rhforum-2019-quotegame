package com.redhat.quotegame.processors;

import com.redhat.quotegame.model.Order;

import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.RuleUnitMemory;
/**
 * @author laurent
 */
public class AggregationService implements RuleUnitMemory {

    private final DataStream<Order> orderStream;

    private final DataStream<String> aggregateStream = DataSource.createStream();

    public AggregationService(DataStream<Order> orderStream) {
        this.orderStream = orderStream;
    }

    public DataStream<String> getAggregateStream() {
        return aggregateStream;
    }

    public DataStream<Order> getOrderStream() {
        return orderStream;
    }
}