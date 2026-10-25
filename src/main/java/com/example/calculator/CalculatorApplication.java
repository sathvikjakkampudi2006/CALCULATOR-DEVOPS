package com.example.calculator;

public class CalculatorApplication {

    public static void main(String[] args) throws Exception {

        CalculatorHttpServer server = new CalculatorHttpServer(8081);

        server.start();
    }
}