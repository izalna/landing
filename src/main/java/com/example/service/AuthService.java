package com.example.service;

import com.example.model.AuthResponse;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AuthService {
    private static final String LOGIN_URL = "https://fakestoreapi.com/auth/login";
    private final OkHttpClient client;
    private final Gson gson;

    public AuthService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public AuthResponse login(String username, String password) throws IOException {
        String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // Log the response body for debugging failed logins
                String responseBody = response.body() != null ? response.body().string() : "No body";
                System.err.println("Login failed: HTTP " + response.code() + " " + response.message());
                System.err.println("Response body: " + responseBody);
                return null;
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, AuthResponse.class);
        }
    }
}