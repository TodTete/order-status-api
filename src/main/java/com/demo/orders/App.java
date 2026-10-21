package com.demo.orders;

public class App {
    public static void main(String[] args) {
        OrderStatusNormalizer normalizer = new OrderStatusNormalizer();
        String input = args.length > 0 ? args[0] : "  shipped  ";
        System.out.println("Entrada:    [" + input + "]");
        System.out.println("Normalizado: [" + normalizer.normalize(input) + "]");
    }
}
