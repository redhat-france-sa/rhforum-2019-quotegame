package com.redhat.quotegame.chaosmonkey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
/**
 * This bean holds scheduled methods that introduce changes into quotes.
 * @author laurent
 */
public class ChaosMonkeyScheduler {

    private final Logger logger = Logger.getLogger(getClass());

    @ConfigProperty(name = "chaos.symbols")
    String symbols;

    @ConfigProperty(name = "chaos.variation.min")
    int minVariation;

    @ConfigProperty(name = "chaos.variation.max")
    int maxVariation;

    List<String> symbolsList = new ArrayList<>();

    @Inject
    @RestClient
    QuoteAPIService quoteAPIService;

    @Scheduled(every = "{chaos.frequency}")
    public void makeChaos() {
        logger.info("Make chaos!! - " + LocalDateTime.now());
        Random generator = new Random();

        // Find symbols to change and its current price.
        if (symbolsList.isEmpty()) {
            StringTokenizer st = new StringTokenizer(symbols, ", ");
            while (st.hasMoreTokens()) {
                symbolsList.add(st.nextToken().trim());
            }
        }
        String symbol = symbolsList.get(generator.nextInt(symbolsList.size()));
        Double currentPrice = quoteAPIService.getQuotePrice(symbol);
        logger.debug("Applying chaos on symbol " + symbol + " with current price " + currentPrice);

        // Now compute a variation within defined range.
        int variation = generator.nextInt(maxVariation - minVariation) + minVariation;
        variation = variation * (generator.nextBoolean() ? -1 : 1);
        logger.debug("Variation: " + variation);

        double newPrice = currentPrice;
        if (variation != 0) {
            newPrice = currentPrice * (1d + (variation / 100d));
        }
        newPrice = round(newPrice, 2);
        logger.debug("Applying a new price " + newPrice);
        quoteAPIService.updateQuotePrice(symbol, newPrice);
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
    
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}