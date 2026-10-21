package com.demo.orders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStatusNormalizerTest {

    private final OrderStatusNormalizer normalizer = new OrderStatusNormalizer();

    @Test
    void convierteAMayusculas() {
        assertEquals("SHIPPED", normalizer.normalize("shipped"));
    }

    @Test
    void rechazaEstadoNulo() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(null));
    }
}
