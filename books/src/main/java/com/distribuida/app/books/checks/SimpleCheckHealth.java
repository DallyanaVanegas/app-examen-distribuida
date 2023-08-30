package com.distribuida.app.books.checks;

import com.distribuida.app.books.clients.AuthorsRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Liveness
public class SimpleCheckHealth implements HealthCheck {
    @Inject
    @RestClient
    AuthorsRestClient clientAuthors;

    @Override
    public HealthCheckResponse call() {
        try {
            clientAuthors.getById(1);
        }
        catch (WebApplicationException ex) {
            ex.printStackTrace();
        }

        return HealthCheckResponse.up("Authors service");
    }
}
