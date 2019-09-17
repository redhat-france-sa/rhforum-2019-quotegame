package com.redhat.quotegame.processors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.quotegame.model.Order;
import com.redhat.quotegame.model.Quote;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.runtime.Remote;

@ApplicationScoped
/**
 * Component that takes care of processing orders and updating portfolio 
 * @author laurent
 */
public class QuotePriceUpdater {
 
    private final Logger logger = Logger.getLogger(getClass());
    
    @Inject
    @Remote("quotegame-quotes")
    RemoteCache<String, Quote> quotesCache;

    @Incoming("orders-4-quoteprice")
    public void computeQuotePrices(Order order) {
        logger.info("Get new order to process...");

    }
}