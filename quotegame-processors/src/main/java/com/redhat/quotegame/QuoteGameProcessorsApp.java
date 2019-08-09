package com.redhat.quotegame.processors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class QuoteGameProcessorsApp {

    private static final Logger logger = LoggerFactory.getLogger("QuoteGameProcessorsApp");

    @Inject
    RemoteCacheManager cacheManager;

    void onStart(@Observes StartupEvent ev) {
        logger.info("Create or get caches named quotegame-portfolios, quotegame-quotes with the default configuration");
        cacheManager.administration().getOrCreateCache("quotegame-portfolios", "default");
        RemoteCache<String, Double> quotesCache = cacheManager.administration().getOrCreateCache("quotegame-quotes", "default");
        // Put initialization values if empty.
        if (quotesCache.isEmpty()) {
            quotesCache.put("RHT", 187.71);
            quotesCache.put("IBM", 140.57);
        }
    }
}