package com.redhat.quotegame;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class QuoteGameApp {

    private static final Logger logger = LoggerFactory.getLogger("QuoteGameApp");

    @Inject
    RemoteCacheManager cacheManager;

    void onStart(@Observes StartupEvent ev) {
        logger.info("Create or get caches named quotegame-users, quotegame-portfolios with the default configuration");
        cacheManager.administration().getOrCreateCache("quotegame-users", "default");
        cacheManager.administration().getOrCreateCache("quotegame-portfolios", "default");
    }
}