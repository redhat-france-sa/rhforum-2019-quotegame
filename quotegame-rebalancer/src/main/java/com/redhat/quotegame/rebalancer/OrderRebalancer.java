package com.redhat.quotegame.rebalancer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.quotegame.model.Order;
import com.redhat.quotegame.model.Quote;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.apache.camel.builder.RouteBuilder;
import org.jboss.logging.Logger;

@ApplicationScoped
/**
 * Component that takes care of rebalancing orders to a unique REST consumer
 * @author laurent
 */
public class OrderRebalancer extends RouteBuilder {

    private final Logger logger = Logger.getLogger(getClass());

    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092") 
    String kafkaBootstrapServers;

    @ConfigProperty(name = "priceUpdater.endpoints", defaultValue = "localhost:8083")
    String[] priceUpdaterEndpoints;

    @Override
    public void configure() throws Exception {
        from("kafka:quotegame-orders?brokers=" + kafkaBootstrapServers 
                + "&groupId=orders-4-quoteprice" 
                + "&valueDeserializer=com.redhat.quotegame.util.OrderDeserializer")
            .log("Got ${body}")
            .marshal().json()
            .loadBalance()
            .failover()
            .to(priceUpdaterEndpoints);
    }
}