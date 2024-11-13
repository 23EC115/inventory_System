package com.ordersystem.interfaces;

import com.ordersystem.models.Order;
import com.ordersystem.exceptions.OutOfStockException;
import com.ordersystem.exceptions.InvalidOrderException;

public interface OrderProcessable {
    // Method to place an order
    void placeOrder(Order order) throws OutOfStockException, InvalidOrderException;

    // Method to update stock levels after an order
    void updateStockLevel(int productId, int quantity);

    // Method to generate a receipt for the order
    void generateOrderReceipt(Order order);
}
