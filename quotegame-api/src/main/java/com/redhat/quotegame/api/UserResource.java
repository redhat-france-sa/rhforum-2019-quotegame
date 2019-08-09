package com.redhat.quotegame.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.infinispan.client.runtime.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import com.redhat.quotegame.model.Portfolio;
import com.redhat.quotegame.model.User;

@Path("/api/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
/**
 * REST resource for managing users.
 * @author laurent
 */
public class UserResource {

    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    @Remote("quotegame-users")
    RemoteCache<String, User> cache;

    @Inject
    @Remote("quotegame-portfolios")
    RemoteCache<String, Portfolio> portfoliosCache;

    @POST
    public Response register(User user) {
        logger.debug("Try creating user using cache " + cache);
        User existingUser = cache.get(user.getName());
        if (existingUser == null) {
            cache.put(user.getName(), user);
            portfoliosCache.put(user.getName(), new Portfolio(user.getName(), 1000D));
            return Response.ok(user).build();
        }
        return Response.status(400, "User with same name already registered").build();
    }

    @GET
    public List<User> getAllUsers() {
		return cache.values().stream()
            .collect(Collectors.toList());
    }

    @DELETE
    @Path("/{name}")
    public Response unregisterUser(@PathParam("name") String name) {
        logger.debugf("Removing {} from caches", name);
        cache.remove(name);
        portfoliosCache.remove(name);
        return Response.ok().build();
    }
}