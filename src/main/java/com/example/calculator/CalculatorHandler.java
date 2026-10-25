package com.example.calculator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalculatorHandler implements HttpHandler {

    private final Calculator calculator = new Calculator();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "{\"error\":\"Only GET requests are allowed\"}");
                return;
            }

            String path = exchange.getRequestURI().getPath();

            // Only GET requests are allowed.
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

                sendResponse(
                        exchange,
                        405,
                        "{\"error\":\"Only GET requests are allowed\"}"
                );

                return;
            }

            // Health endpoint does not need parameters.
            if (path.equals("/actuator/health")) {

                sendResponse(
                        exchange,
                        200,
                        "{\"status\":\"UP\"}"
                );

                return;
            }

            // Check that the endpoint is one of our calculator operations.
            String operation;

            switch (path) {

                case "/api/v1/add":
                    operation = "add";
                    break;

                case "/api/v1/subtract":
                    operation = "subtract";
                    break;

                case "/api/v1/multiply":
                    operation = "multiply";
                    break;

                case "/api/v1/divide":
                    operation = "divide";
                    break;

                default:

                    sendResponse(
                            exchange,
                            404,
                            "{\"error\":\"Endpoint not found\"}"
                    );

                    return;
            }

            // Parse query parameters.
            Map<String, String> parameters =
                    parseQueryParameters(exchange.getRequestURI());

            // Both parameters are required.
            if (!parameters.containsKey("a")
                    || !parameters.containsKey("b")) {

                sendResponse(
                        exchange,
                        400,
                        "{\"error\":\"Parameters 'a' and 'b' are required\"}"
                );

                return;
            }

            double a;
            double b;

            // Convert parameters to numbers.
            try {

                a = Double.parseDouble(parameters.get("a"));
                b = Double.parseDouble(parameters.get("b"));

            } catch (NumberFormatException e) {

                sendResponse(
                        exchange,
                        400,
                        "{\"error\":\"Parameters 'a' and 'b' must be numeric\"}"
                );

                return;
            }

            // Reject NaN and infinity.
            if (!Double.isFinite(a) || !Double.isFinite(b)) {

                sendResponse(
                        exchange,
                        400,
                        "{\"error\":\"Parameters 'a' and 'b' must be finite numbers\"}"
                );

                return;
            }

            double result;

            // Perform the requested operation.
            switch (operation) {

                case "add":
                    result = calculator.add(a, b);
                    break;

                case "subtract":
                    result = calculator.subtract(a, b);
                    break;

                case "multiply":
                    result = calculator.multiply(a, b);
                    break;

                case "divide":
                    result = calculator.divide(a, b);
                    break;

                default:
                    sendResponse(
                            exchange,
                            404,
                            "{\"error\":\"Endpoint not found\"}"
                    );
                    return;
            }

            String json = String.format(
                    Locale.ROOT,
                    "{\"operation\":\"%s\",\"a\":%s,\"b\":%s,\"result\":%s}",
                    operation,
                    a,
                    b,
                    result
            );

            sendResponse(exchange, 200, json);

        } catch (IllegalArgumentException e) {

            // Handles division by zero.
            sendResponse(
                    exchange,
                    400,
                    "{\"error\":\"" + e.getMessage() + "\"}"
            );
        }
    }

    private Map<String, String> parseQueryParameters(URI uri) {

        Map<String, String> parameters = new HashMap<>();

        String query = uri.getRawQuery();

        if (query == null || query.isEmpty()) {
            return parameters;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {

            String[] keyValue = pair.split("=", 2);

            if (keyValue.length == 2) {

                String key = URLDecoder.decode(
                        keyValue[0],
                        StandardCharsets.UTF_8
                );

                String value = URLDecoder.decode(
                        keyValue[1],
                        StandardCharsets.UTF_8
                );

                parameters.put(key, value);
            }
        }

        return parameters;
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        try (OutputStream outputStream =
                     exchange.getResponseBody()) {

            outputStream.write(responseBytes);
        }
    }
}