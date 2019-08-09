package com.redhat.quotegame.processors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.quotegame.model.Order;
import com.redhat.quotegame.model.OrderType;
import com.redhat.quotegame.model.Portfolio;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.runtime.Remote;

@ApplicationScoped
/**
 * Component that takes care of processing orders and updating portfolio 
 * @author laurent
 */
public class PortfolioUpdater {
 
    private final Logger logger = Logger.getLogger(getClass());
    
    @Inject
    @Remote("quotegame-quotes")
    RemoteCache<String, Double> quotesCache;

    @Inject
    @Remote("quotegame-portfolios")
    RemoteCache<String, Portfolio> portfoliosCache;
    
    @Incoming("orders-4-portfolio")
    public void updatePortfolio(Order order) {
        logger.info("Get new order to process...");
        Portfolio portfolio = portfoliosCache.get(order.getUsername());

        if (portfolio != null) {
            logger.infof("Processing order %d for user %s", order.getTimestamp(), order.getUsername());

            Double totalPrice = order.getPrice() * order.getNumber();
            Long numberOfQuotes = portfolio.getQuotes().get(order.getQuote());
            if (numberOfQuotes == null) {
                numberOfQuotes = 0L;
            }

            if (order.getOrderType().equals(OrderType.BUY)) {
                portfolio.setMoney(portfolio.getMoney() - totalPrice);
                portfolio.getQuotes().put(order.getQuote(), numberOfQuotes + order.getNumber());
            } else {
                portfolio.setMoney(portfolio.getMoney() + totalPrice);
                portfolio.getQuotes().put(order.getQuote(), numberOfQuotes - order.getNumber());
            }
            portfoliosCache.replace(portfolio.getUsername(), portfolio);
        }
    }
}