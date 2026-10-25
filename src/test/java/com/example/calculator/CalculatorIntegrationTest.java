package com.example.calculator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatorIntegrationTest {

    private static CalculatorHttpServer server;
    private static HttpClient client;
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws Exception {
        server = new CalculatorHttpServer(0);
        server.start();

        client = HttpClient.newHttpClient();

        baseUrl = "http://localhost:" + server.getPort();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void testAddEndpoint() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/add?a=2&b=3"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(200, response.statusCode());

        assertTrue(response.body().contains("\"operation\":\"add\""));
        assertTrue(response.body().contains("\"result\":5.0"));
    }

    @Test
    void testHealthEndpoint() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/actuator/health"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(200, response.statusCode());

        assertTrue(response.body().contains("\"status\":\"UP\""));
    }
    @Test
    void testPostMethodNotAllowed() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/add?a=2&b=3"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    
        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );
    
        assertEquals(405, response.statusCode());
    
        assertTrue(
                response.body().contains("Only GET requests are allowed")
        );
    }
}