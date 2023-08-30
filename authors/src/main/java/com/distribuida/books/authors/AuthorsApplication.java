package com.distribuida.books.authors;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title="Books API",
                version = "1.0.0",
                contact = @Contact(
                        name = "Dallyana Vanegas",
                        url = "http://davanegas.com/contact",
                        email = "davanegas@uce.edu.ec"),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class AuthorsApplication extends Application {

}
