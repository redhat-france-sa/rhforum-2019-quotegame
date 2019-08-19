package com.redhat.quotegame.processors;

import com.redhat.quotegame.model.Order;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.RuleUnitMemory;

public class QuotePriceUpdaterService implements RuleUnitMemory {
    private DataStream<Order> orderStream;

    public QuotePriceUpdaterService() {
        this( DataSource.createStream());
    }

    public QuotePriceUpdaterService(DataStream<Order> orderStream ) {
        this.orderStream = orderStream;
    }

    public DataStream<Order> getOrderStream() {
        return orderStream;
    }

}
