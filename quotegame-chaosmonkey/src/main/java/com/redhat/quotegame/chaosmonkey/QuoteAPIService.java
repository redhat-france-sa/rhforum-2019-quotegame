package com.redhat.quotegame.chaosmonkey;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/api/quote")
@RegisterRestClient
@RegisterClientHeaders
/**
 * @author laurent
 */
public interface QuoteAPIService {

    @PUT
    @Path("/{symbol}")
    @Produces("application/json")
    Double updateQuotePrice(@PathParam("symbol") String symbol, Double price);
}