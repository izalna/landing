package com.example.model.cart;

import java.time.LocalDate;
import java.util.List;

public class Cart {
    private int id;
    private int userId;
    private String date; // LocalDate would be better, but String matches API response
    private List<CartProduct> products;
    private int __v; // API specific field

    // Constructor (optional, for convenience)
    public Cart(int userId, String date, List<CartProduct> products) {
        this.userId = userId;
        this.date = date;
        this.products = products;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", date='" + date + '\'' +
                ", products=" + products +
                ", __v=" + __v +
                '}';
    }
}
