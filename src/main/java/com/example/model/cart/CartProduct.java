package com.example.model.cart;

public class CartProduct {
    private int productId;
    private int quantity;

    // Constructor (for adding to cart)
    public CartProduct(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartProduct{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}