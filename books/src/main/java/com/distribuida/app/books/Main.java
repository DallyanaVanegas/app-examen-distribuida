package com.distribuida.app.books;

import io.helidon.microprofile.server.Server;

import java.io.IOException;

public final class Main {

    private Main() { } 

    public static void main(final String[] args) throws IOException {
        Server server = startServer();
        System.out.println("http://localhost:" + server.port() + "/books");
    }

    static Server startServer() {
        return Server.create().start(); 
    }

}