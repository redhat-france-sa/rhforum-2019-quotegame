package com.redhat.quotegame;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.quotegame.model.Quote;

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
        
        RemoteCache<String, Quote> quotesCache = cacheManager.administration().getOrCreateCache("quotegame-quotes", "default");
        // Put initialization values if empty.
        if (quotesCache.isEmpty()) {
            quotesCache.put("TYR", new Quote("TYR", 187.71));
            quotesCache.put("CYB", new Quote("CYB", 140.57));
        }
    }
}