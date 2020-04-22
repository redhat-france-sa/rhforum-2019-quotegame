package com.redhat.quotegame.processors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.redhat.quotegame.model.Order;
import com.redhat.quotegame.model.Quote;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.Remote;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
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
    @Named("quotePriceKS")
    QuotePriceUpdater( KieRuntimeBuilder runtimeBuilder ) {
        ksession = runtimeBuilder.newKieSession();
    }

    @Inject
    @Remote("quotegame-quotes")
    RemoteCache<String, Quote> quotesCache;

    @Incoming("orders-4-quoteprice")
    public void computeQuotePrices(Order order) {
        logger.info("Get new order to process...");

        logger.info("Get corresponding Quote ...");
        Quote quote = quotesCache.get(order.getQuote());
        int quoteBefore = quote.hashCode();

        logger.info("Inserting Order fact ...");
        ksession.insert(order);

        logger.info("Inserting Quote fact ...");
        FactHandle quoteFH = ksession.insert(quote);

        logger.info("Fire Rules ...");
        int rulesFired = ksession.fireAllRules();
        logger.info("Number of rules fired = " + rulesFired);

        int quoteAfter = quote.hashCode();

        if (quoteBefore == quoteAfter){
            logger.info("Quote not modified");
        } else {
            logger.info("Updating Quote cache");
            quotesCache.replace(order.getQuote(), quote);
        }

        ksession.delete(quoteFH);
        logger.info("Number of facts in WM = " + ksession.getFactCount());
    }
}