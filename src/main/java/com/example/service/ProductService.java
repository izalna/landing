// src/main/java/com/example/service/ProductService.java
package com.example.service;

import com.example.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProductService {
    private static final String API_URL = "https://fakestoreapi.com/products";
    private final OkHttpClient client;
    private final Gson gson;

    public ProductService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public List<Product> fetchProducts() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch products: HTTP error " + response.code() + " - " + response.message());
            }
            String json = response.body().string();
            Type productListType = new TypeToken<List<Product>>() {}.getType();
            return gson.fromJson(json, productListType);
        }
    }

    public Product fetchProductById(int id) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch product by ID: HTTP error " + response.code() + " - " + response.message());
            }
            String json = response.body().string();
            return gson.fromJson(json, Product.class);
        }
    }

    public Product createProduct(Product product) throws IOException { // Tidak perlu InterruptedException jika OkHttp
        String json = gson.toJson(product);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create product: HTTP error " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, Product.class);
        }
    }

    public Product updateProduct(Product product) throws IOException { // Tidak perlu InterruptedException jika OkHttp
        String json = gson.toJson(product);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL + "/" + product.getId())
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update product: HTTP error " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, Product.class);
        }
    }

    public void deleteProduct(int id) throws IOException { // Tidak perlu InterruptedException jika OkHttp
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete product: HTTP error " + response.code() + " - " + response.message());
            }
            // For Fake Store API, it usually returns 200 OK even for delete, but for real APIs,
            // you might expect 204 No Content.
        }
    }
}