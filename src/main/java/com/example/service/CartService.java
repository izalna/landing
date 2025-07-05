package com.example.service;

import com.example.model.cart.AddCartRequest;
import com.example.model.cart.Cart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CartService {
    private static final String BASE_URL = "https://fakestoreapi.com/carts";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    // Fetches carts for a specific user. Fake Store API returns historical carts.
    public List<Cart> getUserCarts(int userId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/" + userId)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(
                        "Failed to fetch user carts: " + response.code() + " - " + response.body().string());
            }
            if (response.body() == null) {
                return Collections.emptyList();
            }
            Type cartListType = new TypeToken<List<Cart>>() {
            }.getType();
            return gson.fromJson(response.body().charStream(), cartListType);
        }
    }

    // Creates a new cart (or conceptually, saves the current cart state)
    public Cart createCart(AddCartRequest addCartRequest) throws IOException {
        String json = gson.toJson(addCartRequest);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create cart: " + response.code() + " - " + response.body().string());
            }
            if (response.body() == null) {
                throw new IOException("Empty response from server when creating cart.");
            }
            return gson.fromJson(response.body().charStream(), Cart.class);
        }
    }

    // Updates an existing cart
    public Cart updateCart(int cartId, AddCartRequest addCartRequest) throws IOException {
        String json = gson.toJson(addCartRequest);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + cartId)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update cart: " + response.code() + " - " + response.body().string());
            }
            if (response.body() == null) {
                throw new IOException("Empty response from server when updating cart.");
            }
            return gson.fromJson(response.body().charStream(), Cart.class);
        }
    }

    // Deletes a cart
    public void deleteCart(int cartId) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/" + cartId)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete cart: " + response.code() + " - " + response.body().string());
            }
            // No specific return for delete, just check if successful
        }
    }
}