package com.example.service;

import com.example.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserService {
    private static final String API_URL = "https://fakestoreapi.com/users";
    private final OkHttpClient client;
    private final Gson gson;

    public UserService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public List<User> fetchAllUsers() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch all users: HTTP error " + response.code() + " - " + response.message());
            }
            String json = response.body().string();
            Type userListType = new TypeToken<List<User>>() {}.getType();
            return gson.fromJson(json, userListType);
        }
    }

    public User fetchUserById(int id) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch user by ID: HTTP error " + response.code() + " - " + response.message());
            }
            String json = response.body().string();
            return gson.fromJson(json, User.class);
        }
    }

    // Helper method to find a user by username after fetching all users
    public User findUserByUsername(String username) throws IOException {
        List<User> users = fetchAllUsers(); // Fetches all users from API
        for (User user : users) {
            if (user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null; // User not found
    }

    public User createUser(User user) throws IOException {
        String json = gson.toJson(user);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to create user: HTTP error " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, User.class);
        }
    }

    public User updateUser(User user) throws IOException {
        if (user.getId() == 0) {
            throw new IllegalArgumentException("User ID must be set for update operation.");
        }
        String json = gson.toJson(user);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL + "/" + user.getId())
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to update user: HTTP error " + response.code() + " - " + response.message());
            }
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, User.class);
        }
    }

    public void deleteUser(int id) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete user: HTTP error " + response.code() + " - " + response.message());
            }
        }
    }
}