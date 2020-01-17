package com.redhat.quotegame.priceupdater;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.redhat.quotegame.model.Order;
import com.redhat.quotegame.model.Quote;
import com.redhat.quotegame.priceupdater.model.OrderListSnapshot;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

//import io.quarkus.infinispan.client.runtime.Remote;
import io.quarkus.infinispan.client.Remote;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.rules.KieRuntimeBuilder;

@Path("/api/order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
/**
 * Component that takes care of processing orders and updating portfolio 
 * @author laurent
 */
public class QuotePriceUpdater {
 
    private final Logger logger = Logger.getLogger(getClass());
    
    private KieSession ksession;

    private ConcurrentLinkedQueue<FactHandle> lastOrdersHandles = new ConcurrentLinkedQueue<>();

    @Inject
    @Named("quotePriceKS")
    QuotePriceUpdater( KieRuntimeBuilder runtimeBuilder ) {
        ksession = runtimeBuilder.newKieSession();
    }

    @Inject
    @Remote("quotegame-quotes")
    RemoteCache<String, Quote> quotesCache;

    @Inject
    KafkaOrderListSnapshotProducerManager orderListSnapshotProducer;

    @Incoming("workingmemory-snapshots-in")
    public void syncWorkingMemory(OrderListSnapshot snapshot) {
        logger.info("Receiving a new WM snapshot produced at " + snapshot.getTimestamp());
        synchronized (ksession) {
            // Remove alder facts and add new ones.
            /*
            for (FactHandle handle : lastOrdersHandles) {
                ksession.delete(handle);
            }
            lastOrdersHandles.clear();
            for (Order order : snapshot.getOrders()) {
                logger.info("Syncing a new Order fact: " + order.toString());
                FactHandle orderFH = ksession.insert(order);
                lastOrdersHandles.add(orderFH);
            }
            logger.info("Number of facts synced in WM = " + ksession.getFactCount());
            */
        }
    }

    public void publishWorkingMemorySnapshot() {
        logger.info("Publishing a new WorkingMemory snapshot...");
        OrderListSnapshot snapshot = new OrderListSnapshot();
        snapshot.setTimestamp(System.currentTimeMillis());
        for (FactHandle handle : lastOrdersHandles) {
            Object object = ksession.getObject(handle);
            if (object instanceof Order) {
                snapshot.addOrder((Order)object);
            }
        }
        orderListSnapshotProducer.publish(snapshot);
    }

    @POST
    public Response considerOrder(Order order) {
        logger.info("Get new order to process...");

        logger.info("Get corresponding Quote ...");
        Quote quote = quotesCache.get(order.getQuote());
        int quoteBefore = quote.hashCode();

        logger.info("Inserting Order fact: " + order);
        FactHandle orderFH = ksession.insert(order);
        lastOrdersHandles.add(orderFH);

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
        if (ksession.getFactCount() > 5) {
            ksession.delete(lastOrdersHandles.remove());
        }
        
        // Now that udpated the status from working memory (either from the
        // rules or by cleaning the number of fact counts), we should build
        // a snapshot and pubnlish it on Kafka.
        publishWorkingMemorySnapshot();

        logger.info("Returning response ok");
        return Response.ok(order).build();
    }
}