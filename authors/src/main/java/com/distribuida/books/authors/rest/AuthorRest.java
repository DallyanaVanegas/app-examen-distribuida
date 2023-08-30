package com.distribuida.books.authors.rest;

import com.distribuida.Authors.authors.repo.AuthorRepository;
import com.distribuida.books.authors.db.Author;
import jakarta.inject.Inject;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.util.List;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(info = @Info(title = "Author API", version = "1.0"))
@Transactional
public class AuthorRest {

    @Inject
    AuthorRepository rep;

    @Retry(maxRetries = 3)
    @CircuitBreaker(failOn = Throwable.class, delay = 2000, requestVolumeThreshold = 4, failureRatio = 0.5)
    @APIResponse(responseCode = "200", description = "Lista de autores",
            content = @Content(schema = @Schema(implementation = Author.class)))
    @GET
    public List<Author> findAll() {
        return rep.findAll();
    }

    @Timeout(1000)
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Autor por ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(
                                    ref = "Autor encontrado"))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Autor no encontrado.")
    })
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        var author = rep.findById(id);
        if (author == null) {
            throw new NotFoundException("Autor no encontrado");
        }

        return Response.ok(author).build();
    }

    @Fallback(fallbackMethod = "createFallback")
    @Retry(maxRetries = 2)
    @Operation(summary = "Crear un nuevo autor")
    @APIResponse(responseCode = "201", description = "Autor creado")
    @POST
    public Response create(Author p) {
        rep.create(p);

        return Response.status(Response.Status.CREATED.getStatusCode(), "author created").build();
    }

    @Operation(summary = "Actualizar un autor por ID")
    @APIResponse(responseCode = "200", description = "Autor actualizado")
    @APIResponse(responseCode = "404", description = "Autor no encontrado")
    @Retry(maxRetries = 2)
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, Author authorObj) {
        Author author = rep.findById(id);
        if (author == null) {
            throw new NotFoundException("Autor no encontrado");
        }

        author.setFirstName(authorObj.getFirstName());
        author.setLastName(authorObj.getLastName());

        return Response.ok().build();
    }

    @Fallback(fallbackMethod = "deleteFallback")
    @CircuitBreaker(failOn = Throwable.class, requestVolumeThreshold = 5, failureRatio = 0.5, delay = 5000)
    @Operation(summary = "Eliminar un autor por ID")
    @APIResponse(responseCode = "200", description = "Autor eliminado")
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        try {
            rep.delete(id);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity("No se pudo eliminar el autor").build();
        }
    }

    private Response createFallback(Author p) {
        return Response.serverError().entity("No se pudo crear el autor").build();
    }

    private Response deleteFallback(Long id) {
        return Response.serverError().entity("No se pudo eliminar el autor").build();
    }
}
