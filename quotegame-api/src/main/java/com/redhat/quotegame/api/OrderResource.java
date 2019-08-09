package com.redhat.quotegame.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.redhat.quotegame.KafkaOrderProducerManager;
import com.redhat.quotegame.model.Order;

import org.jboss.logging.Logger;

@Path("/api/order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
/**
 * REST resource for creating orders.
 * @author laurent
 */
public class OrderResource {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    KafkaOrderProducerManager orderProducer;

    @POST
    public Response createOrder(Order order) {
        logger.debugf("Publishing new order for %s", order.getUsername());
        if (validateOrder(order)) {
            orderProducer.publish(order);
            return Response.ok(order).build();
        }
        return Response.status(400, "Order is not valid").build();
    }

    private boolean validateOrder(Order order) {
        if (order.getUsername() == null || order.getOrderType() == null || order.getQuote() == null
                || order.getNumber() == null || order.getPrice() == null) {
            return false;
        }
        order.setTimestamp(System.currentTimeMillis());
        return true;
    }
}