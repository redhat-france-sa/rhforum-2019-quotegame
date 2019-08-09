package com.redhat.quotegame.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.infinispan.client.runtime.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import com.redhat.quotegame.model.Portfolio;

@Path("/api/portfolio")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
/**
 * REST resource for managing user's portfolio.
 * @author laurent
 */
public class PortfolioResource {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    @Remote("quotegame-portfolios")
    RemoteCache<String, Portfolio> portfoliosCache;

    @GET
    @Path("/{username}")
    public Response getByUser(@PathParam("username") String username) {
        logger.infof("Retrieving portfolio for user '%s'", username);
        Portfolio portfolio = portfoliosCache.get(username);
        if (portfolio != null) {
            return Response.ok(portfolio).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    public List<Portfolio> getAllPortfolios() {
        return portfoliosCache.values().stream()
            .collect(Collectors.toList());
    }
}