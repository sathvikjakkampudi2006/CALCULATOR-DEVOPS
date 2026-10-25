package com.example.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void testAddition() {
        assertEquals(5.0, calculator.add(2, 3));
    }

    @Test
    void testSubtraction() {
        assertEquals(2.0, calculator.subtract(5, 3));
    }

    @Test
    void testMultiplication() {
        assertEquals(6.0, calculator.multiply(2, 3));
    }

    @Test
    void testDivision() {
        assertEquals(2.0, calculator.divide(6, 3));
    }

    @Test
    void testDivisionByZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(10, 0)
        );
    }
}