package com.ordersystem;

import com.ordersystem.database.DatabaseManager;
import com.ordersystem.models.Customer;
import com.ordersystem.models.Product;
import com.ordersystem.models.Order;
import com.ordersystem.services.OrderService;
import com.ordersystem.exceptions.OutOfStockException;
import com.ordersystem.exceptions.InvalidOrderException;

public class MainApp {
    public static void main(String[] args) {
        // Initialize the database and inventory
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();  // Create tables if they don't exist
        OrderService orderService = new OrderService(dbManager, new com.ordersystem.models.Inventory());

        // Create a new customer
        Customer customer = new Customer(1, "John Doe", "johndoe@example.com");
        dbManager.addCustomer(customer);  // Add the customer to the database

        // Create a new product
        Product product = new Product(1, "Laptop", "Electronics", 1000.00, 10);
        dbManager.updateProductStock(product.getId(), 10);  // Add the product to the inventory

        // Display initial product details
        System.out.println("Available Product: " + product.getName());
        System.out.println("Stock: " + product.getStock());

        // Simulate an order for the product
        Order order = new Order(1, customer, product, 2, 0, null); // Customer ordering 2 laptops

        try {
            orderService.placeOrder(order);  // Place the order
        } catch (OutOfStockException | InvalidOrderException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Display updated product details after order
        Product updatedProduct = dbManager.getProductById(product.getId());
        System.out.println("\nUpdated Product after Order:");
        System.out.println("Product: " + updatedProduct.getName());
        System.out.println("Remaining Stock: " + updatedProduct.getStock());

        // Display order confirmation (receipt is generated automatically in the service)
    }
}
