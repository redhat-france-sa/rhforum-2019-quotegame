package com.redhat.quotegame;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.kafka.common.errors.TimeoutException;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
/**
 * Initializes caches used later by the other resources.
 * @author laurent
 */
public class QuoteGameApp {

    private static final Logger logger = LoggerFactory.getLogger("QuoteGameApp");

    private CountDownLatch waitUntilStarted = new CountDownLatch(1);

    @Inject
    RemoteCacheManager cacheManager;

    void onStart(@Observes StartupEvent ev) {
        logger.info("Create or get caches named quotegame-users, quotegame-portfolios with the default configuration");
        cacheManager.administration().getOrCreateCache("quotegame-users", "default");
        cacheManager.administration().getOrCreateCache("quotegame-portfolios", "default");

        waitUntilStarted.countDown();
    }

    public void ensureStart() {
        try {
            if (!waitUntilStarted.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException(new TimeoutException());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}