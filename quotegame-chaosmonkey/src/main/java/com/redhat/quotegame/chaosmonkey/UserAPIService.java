package com.redhat.quotegame.chaosmonkey;

import com.redhat.quotegame.model.User;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/api/user")
@RegisterRestClient
@RegisterClientHeaders
/**
 * @author laurent
 */
public interface UserAPIService {

    @POST
    @Produces("application/json")
    User register(User user);
}