package com.redhat.quotegame.chaosmonkey;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
/**
 * @author laurent
 */
public class ChaosMonkeyScheduler {

    @Inject
    @RestClient
    UserAPIService userAPIService;

    @Inject
    @RestClient
    QuoteAPIService quoteAPIService;

    @Scheduled(every = "{chaos.frequency}")
    public void makeChaos() {
        System.out.println("Make chaos!! - " + LocalDateTime.now());
    }
}