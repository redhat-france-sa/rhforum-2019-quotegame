package com.redhat.quotegame.processors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.quotegame.model.Order;
import com.redhat.quotegame.model.OrderType;

import com.redhat.quotegame.model.Quote;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.runtime.Remote;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.kogito.rules.KieRuntimeBuilder;

@ApplicationScoped
/**
 * Component that takes care of processing orders and updating portfolio 
 * @author laurent
 */
public class QuotePriceUpdater {
 
    private final Logger logger = Logger.getLogger(getClass());
    private KieSession ksession;

    @Inject
    QuotePriceUpdater( KieRuntimeBuilder runtimeBuilder ) {
//        ksession = runtimeBuilder.newKieSession("myStatelessKsession");
        ksession = runtimeBuilder.newKieSession("myStatefulKsession");
    }

    @Inject
    @Remote("quotegame-quotes")
    RemoteCache<String, Quote> quotesCache;

    @Incoming("orders-4-quoteprice")
    public void computeQuotePrices(Order order) {
        logger.info("Get new order to process ...");

        logger.info("Get corresponding Quote ...");
        Quote quote = quotesCache.get(order.getQuote());

        logger.info("Inserting Order fact ...");
        EntryPoint orderStream = ksession.getEntryPoint("Order Stream");
        orderStream.insert(order);

        logger.info("Inserting Quote fact ...");
        ksession.insert(quote);

        logger.info("Fire Rules ...");
        ksession.fireAllRules();

        logger.info("number of facts in WM = "+ksession.getFactCount());
    }
}