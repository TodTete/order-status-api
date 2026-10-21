package com.demo.orders;

/**
 * Normaliza los estados de pedido que recibe la API.
 */
public class OrderStatusNormalizer {

    /**
     * Devuelve el estado en el formato acordado (MAYÚSCULAS).
     *
     * @param rawStatus estado tal como llega del cliente
     * @return estado normalizado
     */
    public String normalize(String rawStatus) {
        if (rawStatus == null) {
            throw new IllegalArgumentException("El estado del pedido no puede ser nulo");
        }
        return rawStatus.toUpperCase();
    }
}
