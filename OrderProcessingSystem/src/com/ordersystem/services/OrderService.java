package com.ordersystem.services;

import com.ordersystem.models.Customer;
import com.ordersystem.models.Order;
import com.ordersystem.models.Product;
import com.ordersystem.models.Inventory;
import com.ordersystem.database.DatabaseManager;
import com.ordersystem.interfaces.OrderProcessable;
import com.ordersystem.exceptions.OutOfStockException;
import com.ordersystem.exceptions.InvalidOrderException;

import java.util.Date;

public class OrderService implements OrderProcessable {
    private DatabaseManager dbManager;
    private Inventory inventory;

    public OrderService(DatabaseManager dbManager, Inventory inventory) {
        this.dbManager = dbManager;
        this.inventory = inventory;
    }

    @Override
    public void placeOrder(Order order) throws OutOfStockException, InvalidOrderException {
        Product product = dbManager.getProductById(order.getProduct().getId());
        
        // Check if the product exists in the database
        if (product == null) {
            throw new InvalidOrderException("Product not found.");
        }

        // Check if there is enough stock
        if (product.getStock() < order.getQuantity()) {
            throw new OutOfStockException("Not enough stock available.");
        }

        // Calculate total price and set it in the order
        double totalPrice = product.getPrice() * order.getQuantity();
        order.setTotalPrice(totalPrice);
        order.setOrderDate(new Date());

        // Deduct the stock level and update the database
        updateStockLevel(product.getId(), order.getQuantity());

        // Save the order in the database
        dbManager.addOrder(order);

        // Generate and display the order receipt
        generateOrderReceipt(order);
    }

    @Override
    public void updateStockLevel(int productId, int quantity) {
        // Get the product and update its stock in inventory and database
        Product product = dbManager.getProductById(productId);
        if (product != null) {
            int updatedStock = product.getStock() - quantity;
            product.setStock(updatedStock);
            dbManager.updateProductStock(productId, updatedStock);
        }
    }

    @Override
    public void generateOrderReceipt(Order order) {
        // Basic receipt generation
        System.out.println("=== Order Receipt ===");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Customer: " + order.getCustomer().getName());
        System.out.println("Product: " + order.getProduct().getName());
        System.out.println("Quantity: " + order.getQuantity());
        System.out.println("Total Price: $" + order.getTotalPrice());
        System.out.println("Order Date: " + order.getOrderDate());
        System.out.println("=====================");
    }

    // Additional method to add a new customer
    public void addCustomer(Customer customer) {
        dbManager.addCustomer(customer);
    }

    // Additional method to check the stock level of a product
    public boolean isProductInStock(int productId, int quantity) {
        Product product = dbManager.getProductById(productId);
        return product != null && product.getStock() >= quantity;
    }
}
