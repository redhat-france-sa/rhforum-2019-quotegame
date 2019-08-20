package com.redhat.quotegame.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.protostream.MessageMarshaller;

import com.redhat.quotegame.util.PortfolioMarshaller;
import com.redhat.quotegame.util.QuoteMarshaller;
import com.redhat.quotegame.util.QuotesMapEntryMarshaller;
import com.redhat.quotegame.util.UserMarshaller;

/**
 * Handles configuration of marshalling code for marshalling
 * @author laurent
 */
@ApplicationScoped
public class MarshallerConfiguration {

    @Produces
    MessageMarshaller userMarshaller() {
        return new UserMarshaller();
    }

    @Produces
    MessageMarshaller portfolioMarshaller() {
        return new PortfolioMarshaller();
    }

    @Produces
    MessageMarshaller quotesMapEntryMarshaller() {
        return new QuotesMapEntryMarshaller();
    }

    @Produces
    MessageMarshaller quoteMarshaller() {
        return new QuoteMarshaller();
    }
}