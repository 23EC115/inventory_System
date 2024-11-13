package com.ordersystem.database;

import com.ordersystem.models.Product;
import com.ordersystem.models.Order;
import com.ordersystem.models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/order_system";  // Change to your DB URL
    private static final String USER = "root";  // Change to your DB username
    private static final String PASSWORD = "password";  // Change to your DB password

    private Connection connection;

    // Establish a connection to the MySQL database
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Initialize the database (create tables if not already created)
    public void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String createProductTable = "CREATE TABLE IF NOT EXISTS products (" +
                                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                                        "name VARCHAR(255)," +
                                        "category VARCHAR(255)," +
                                        "price DOUBLE," +
                                        "stock INT)";
            String createCustomerTable = "CREATE TABLE IF NOT EXISTS customers (" +
                                         "id INT AUTO_INCREMENT PRIMARY KEY," +
                                         "name VARCHAR(255)," +
                                         "email VARCHAR(255))";
            String createOrderTable = "CREATE TABLE IF NOT EXISTS orders (" +
                                      "id INT AUTO_INCREMENT PRIMARY KEY," +
                                      "customer_id INT," +
                                      "product_id INT," +
                                      "quantity INT," +
                                      "total_price DOUBLE," +
                                      "order_date TIMESTAMP," +
                                      "FOREIGN KEY (customer_id) REFERENCES customers(id)," +
                                      "FOREIGN KEY (product_id) REFERENCES products(id))";

            stmt.executeUpdate(createProductTable);
            stmt.executeUpdate(createCustomerTable);
            stmt.executeUpdate(createOrderTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new customer to the database
    public void addCustomer(Customer customer) {
        String query = "INSERT INTO customers (name, email) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new product to the database
    public void addProduct(Product product) {
        String query = "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update product stock in the database
    public void updateProductStock(int productId, int newStock) {
        String query = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve product by ID from the database
    public Product getProductById(int productId) {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add an order to the database
    public void addOrder(Order order) {
        String query = "INSERT INTO orders (customer_id, product_id, quantity, total_price, order_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, order.getCustomer().getId());
            stmt.setInt(2, order.getProduct().getId());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getTotalPrice());
            stmt.setTimestamp(5, new Timestamp(order.getOrderDate().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
