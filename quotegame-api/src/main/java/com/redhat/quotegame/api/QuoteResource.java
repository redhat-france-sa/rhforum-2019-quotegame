package com.redhat.quotegame.api;

import java.time.LocalDateTime;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import io.quarkus.infinispan.client.runtime.Remote;
import io.quarkus.scheduler.Scheduled;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

@Path("/api/quote")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
/**
 * REST resource for managing quotes.
 * @author laurent
 */
public class QuoteResource {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    @Remote("quotegame-quotes")
    RemoteCache<String, Double> quotesCache;

    private Sse sse = null;
    private SseBroadcaster sseBroadcaster = null;
    private OutboundSseEvent.Builder eventBuilder;

    @PUT
    @Path("/{symbol}")
    public Response updateQuotePrice(@PathParam("symbol") String symbol, Double price) {
        Double currentPrice = quotesCache.get(symbol);
        if (currentPrice != null) {
            quotesCache.put(symbol, price);
            return Response.ok(price).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{symbol}")
    public Response getQuotePrice(@PathParam("symbol") String symbol) {
        Double currentPrice = quotesCache.get(symbol);
        if (currentPrice != null) {
            return Response.ok(currentPrice).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/streaming")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void consume(@Context SseEventSink sseEventSink, @Context Sse sse) {
        if (this.sse == null) {
            this.sse = sse;
            this.eventBuilder = sse.newEventBuilder();
        }
        if (this.sseBroadcaster == null) {
            this.sseBroadcaster = sse.newBroadcaster();
        }
        logger.info("Registering a new sseEventSink: " + sseEventSink);
        sseBroadcaster.register(sseEventSink);
    }

    @Scheduled(every = "5s")
    public void publishQuotePrices() {
        logger.debug("Publishing quote prices at " + LocalDateTime.now());

		if (sseBroadcaster != null) {
            // Publish a JSON array with quote prices.
            StringBuilder data = new StringBuilder("[");
            quotesCache.entrySet().forEach(
                entry -> data.append(serializeEntryToJSON(entry)).append(", ")
            );
            // Remove trailing ","
            data.deleteCharAt(data.length() - 2);
            data.append("]");

            logger.debug("Publishing: " + data.toString());
            OutboundSseEvent sseEvent = this.eventBuilder.name("message")
                .id(LocalDateTime.now().toString())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(data.toString())
                .reconnectDelay(3000)
                .build();
            sseBroadcaster.broadcast(sseEvent);
            //sseBroadcaster.broadcast(this.eventBuilder.data(data.toString()).build());
            //sseBroadcaster.broadcast(this.eventBuilder.name("message").data("{\"timestamp\":1566403446588,\"symbol\":\"EUR/USD\",\"bid\":\"1.59861\",\"ask\":\"1.59871\"}").build());	
        }
    }

    private String serializeEntryToJSON(Map.Entry<String, Double> entry) {
		return "{\"symbol\": \"" + entry.getKey() + "\", \"price\": " + entry.getValue() + "}";
    } 
}