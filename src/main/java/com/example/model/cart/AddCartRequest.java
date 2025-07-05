package com.example.model.cart;

import java.time.LocalDate;
import java.util.List;

public class AddCartRequest {
    private int userId;
    private String date; // Format "YYYY-MM-DD"
    private List<CartProduct> products;

    public AddCartRequest(int userId, List<CartProduct> products) {
        this.userId = userId;
        this.products = products;
        this.date = LocalDate.now().toString(); // Set current date
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CartProduct> products) {
        this.products = products;
    }
}