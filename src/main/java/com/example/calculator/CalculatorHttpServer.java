package com.example.calculator;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CalculatorHttpServer {

    private final HttpServer server;

    public CalculatorHttpServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        CalculatorHandler handler = new CalculatorHandler();

        server.createContext("/api/v1/add", handler);
        server.createContext("/api/v1/subtract", handler);
        server.createContext("/api/v1/multiply", handler);
        server.createContext("/api/v1/divide", handler);
        server.createContext("/actuator/health", handler);
    }

    public void start() {
        server.start();
        System.out.println(
                "Calculator server started on port "
                        + server.getAddress().getPort()
        );
    }

    public void stop() {
        server.stop(0);
    }

    public int getPort() {
        return server.getAddress().getPort();
    }
}