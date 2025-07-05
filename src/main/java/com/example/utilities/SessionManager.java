package com.example.utilities;

import com.example.model.User;

public class SessionManager {
    private static User currentUser; // User yang sedang login
    private static String currentToken; // Token autentikasi dari API

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    public static void setCurrentToken(String token) {
        currentToken = token;
    }

    public static boolean isLoggedIn() {
        return currentUser != null && currentToken != null;
    }

    public static void logout() {
        currentUser = null;
        currentToken = null;
    }
}