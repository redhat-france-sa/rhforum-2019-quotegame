package com.redhat.quotegame;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.quotegame.model.Quote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class QuoteGameRebalancerApp {

    private static final Logger logger = LoggerFactory.getLogger("QuoteGameRebalancerApp");

    void onStart(@Observes StartupEvent ev) {
    }
}