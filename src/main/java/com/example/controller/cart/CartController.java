package com.example.controller.cart;

import com.example.model.Product;
import com.example.model.User;
import com.example.service.ProductService;
import com.example.utilities.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CartController {

    @FXML
    private Label cartTitleLabel;
    @FXML
    private ListView<String> cartListView;
    @FXML
    private Label totalLabel;

    private ObservableList<String> cartItems = FXCollections.observableArrayList();

    private static final Map<Integer, Integer> userCart = new HashMap<>();

    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        cartListView.setItems(cartItems);
        updateCartDisplay();
    }

    public void addProductToCart(int productId, int quantity) {
        userCart.merge(productId, quantity, Integer::sum);
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartItems.clear();
        double total = 0;

        if (cartTitleLabel == null) {
            System.err.println("cartTitleLabel is null — check FXML loading!");
            return;
        }

        User currentUser = SessionManager.getCurrentUser();

        String displayName = "Guest";
        if (currentUser != null) {
            displayName = currentUser.getUsername();
            if (currentUser.getName() != null && currentUser.getName().getFirstname() != null) {
                displayName = currentUser.getName().getFirstname();
            }
        }
        cartTitleLabel.setText("Keranjang untuk: " + displayName);

        for (Map.Entry<Integer, Integer> entry : userCart.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            try {
                Product product = productService.fetchProductById(productId);
                if (product != null) {
                    double itemPrice = product.getPrice() * quantity;
                    cartItems.add(String.format("%s (x%d) - $%.2f", product.getTitle(), quantity, itemPrice));
                    total += itemPrice;
                }
            } catch (IOException e) {
                cartItems.add("Gagal memuat produk (ID: " + productId + ")");
                System.err.println("Error fetching product: " + e.getMessage());
            }
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void handleRemoveSelected() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            showAlert(Alert.AlertType.INFORMATION, "Not Implemented",
                    "Hapus per item belum diimplementasikan dengan ID. Gunakan tombol kosongkan keranjang.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan",
                    "Pilih item yang ingin dihapus dari keranjang.");
        }
    }

    @FXML
    private void handleClearCart() {
        userCart.clear();
        updateCartDisplay();
        showAlert(Alert.AlertType.INFORMATION, "Keranjang Dikosongkan",
                "Semua item telah dihapus dari keranjang.");
    }

    @FXML
    private void handleCheckout() {
        if (userCart.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Keranjang Kosong", "Tidak ada item untuk checkout.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Checkout Berhasil",
                "Pesanan Anda berhasil diproses. Terima kasih!");
        userCart.clear();
        updateCartDisplay();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}