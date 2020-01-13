package com.redhat.quotegame.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.jboss.logging.Logger;

@Path("/api/config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
/**
 * REST resource for getting configuratino.
 * @author laurent
 */
public class ConfigResource {

    private final Logger logger = Logger.getLogger(getClass());

    @ConfigProperty(name = "config.environment", defaultValue = "development")
    String environment;

    @ConfigProperty(name = "config.headerColor", defaultValue = "#1d1d1d")
    String headerColor;

    @GET
    public Config getConfig() {
        logger.debug("Retrieving environment configuration for " + environment);
        return new Config(environment, headerColor);
    }

    public class Config {
        private String environment;
        private String headerColor;

        public Config(String environment, String headerColor) {
            this.environment = environment;
            this.headerColor = headerColor;
        }

        public String getEnvironment() {
            return this.environment;
        }
        public String getHeaderColor() {
            return this.headerColor;
        }
    }
}