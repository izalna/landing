package com.example.controller.auth;

import com.example.controller.HomeController;
import com.example.model.AuthResponse;
import com.example.model.User;
import com.example.service.AuthService;
import com.example.service.UserService;
import com.example.utilities.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private HomeController homeController; // Referensi ke HomeController

    // Setter untuk HomeController, dipanggil dari HomeController
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Username dan Password tidak boleh kosong.");
            return;
        }

        try {
            AuthResponse authResponse = authService.login(username, password);

            if (authResponse != null && authResponse.getToken() != null) {
                // Login API berhasil, sekarang ambil detail user dari API
                User loggedInUser = userService.findUserByUsername(username);

                if (loggedInUser != null) {
                    SessionManager.setCurrentUser(loggedInUser); // Simpan objek User lengkap
                    SessionManager.setCurrentToken(authResponse.getToken()); // Simpan token

                    String displayName = (loggedInUser.getName() != null
                            && loggedInUser.getName().getFirstname() != null) ? loggedInUser.getName().getFirstname()
                                    : loggedInUser.getUsername();
                    showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, " + displayName + "!");

                    // Tutup jendela login
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.close();

                    // Panggil updateUIForLoginStatus di HomeController setelah login berhasil
                    if (homeController != null) {
                        homeController.updateUIForLoginStatus();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Gagal",
                            "Informasi user tidak ditemukan setelah login API berhasil. (User mungkin tidak ada di daftar Fake Store API)");
                    SessionManager.logout(); // Pastikan sesi bersih jika user tidak ditemukan
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah. Silakan coba lagi.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Jaringan",
                    "Tidak dapat terhubung ke server atau terjadi masalah API: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Terjadi Kesalahan",
                    "Terjadi kesalahan saat mencoba login: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}