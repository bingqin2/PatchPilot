package demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    @Test
    void should_add_numbers() {
        assertEquals(3, new Calculator().add(1, 2));
    }
}
