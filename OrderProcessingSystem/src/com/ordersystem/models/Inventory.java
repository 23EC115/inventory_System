package com.ordersystem.models;

import java.util.HashMap;

public class Inventory {
    private HashMap<Integer, Product> products;

    public Inventory() {
        products = new HashMap<>();
    }

    // Add a product to inventory
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    // Get a product by ID
    public Product getProductById(int id) {
        return products.get(id);
    }

    // Check if product exists
    public boolean hasProduct(int id) {
        return products.containsKey(id);
    }

    // Update product stock level
    public void updateStock(int id, int quantity) {
        Product product = products.get(id);
        if (product != null) {
            int newStock = product.getStock() - quantity;
            product.setStock(newStock);
        }
    }
}
