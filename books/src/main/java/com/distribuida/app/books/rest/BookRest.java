package com.distribuida.app.books.rest;

import com.distribuida.app.books.clients.AuthorsRestClient;
import com.distribuida.app.books.db.Book;
import com.distribuida.app.books.dtos.AuthorDto;
import com.distribuida.app.books.dtos.BookDto;
import com.distribuida.app.books.repo.BookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@ApplicationScoped
@Schema(name = "BookRest API")
public class BookRest {

    @Inject
    BookRepository rep;

    @Inject
    @RestClient
    AuthorsRestClient clientAuthors;

    static BookDto fromBook(Book obj) {
        BookDto dto = new BookDto();
        dto.setId(obj.getId());
        dto.setIsbn(obj.getIsbn());
        dto.setTitle(obj.getTitle());
        dto.setPrice(obj.getPrice());
        dto.setAuthorId(obj.getAuthorId());
        return dto;
    }

    @Retry(maxRetries = 3)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 2000)
    @GET
    @Operation(
            summary = "Lista todos los libros",
            description = "Lista todos los libros ordenados por nombre")
    public List<BookDto> findAll() {
        List<Book> books = rep.findAll();

        return books.stream()
                .map(BookRest::fromBook)
                .map(dto -> {
                    AuthorDto authorDto = clientAuthors.getById(dto.getAuthorId());
                    String aname = String.format("%s, %s", authorDto.getLastName(),
                            authorDto.getFirstName());
                    dto.setAuthorName(aname);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Timeout(1000)
    @Operation(summary = "Obtener un libro por ID")
    @APIResponse(responseCode = "200", description = "Libro encontrado",
            content = @Content(schema = @Schema(implementation = Book.class)))
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        var book = rep.findById(id);

        if (book == null) {
            throw new NotFoundException("Libro no encontrado");
        }

        BookDto dto = fromBook(book);

        AuthorDto authorDto = clientAuthors.getById(dto.getAuthorId());
        String aname = String.format("%s, %s", authorDto.getLastName(),
                authorDto.getFirstName());
        dto.setAuthorName(aname);

        return Response.ok(dto).build();
    }

    @Fallback(fallbackMethod = "createFallback")
    @Retry(maxRetries = 2)
    @Operation(summary = "Crear un nuevo libro")
    @APIResponse(responseCode = "201", description = "Libro creado")
    @POST
    public Response create(Book p) {
        rep.create(p);

        return Response.status(Response.Status.CREATED.getStatusCode(), "book created").build();
    }

    @Retry(maxRetries = 2)
    @Operation(summary = "Actualizar un libro por ID")
    @APIResponse(responseCode = "200", description = "Libro actualizado")
    @APIResponse(responseCode = "404", description = "Libro no encontrado")
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Book bookObj) {
        Book book = rep.findById(id);

        if (book == null) {
            throw new NotFoundException("Libro no encontrado");
        }

        book.setIsbn(bookObj.getIsbn());
        book.setPrice(bookObj.getPrice());
        book.setTitle(bookObj.getTitle());

        return Response.ok().build();
    }

    @Fallback(fallbackMethod = "deleteFallback")
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 5000)
    @Operation(summary = "Eliminar un libro por ID")
    @APIResponse(responseCode = "200", description = "Libro eliminado")
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            rep.delete(id);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity("No se pudo eliminar el libro").build();
        }
    }

    private Response createFallback(Book p) {
        return Response.serverError().entity("No se pudo crear el libro").build();
    }

    private Response deleteFallback(Long id) {
        return Response.serverError().entity("No se pudo eliminar el libro").build();
    }


}
