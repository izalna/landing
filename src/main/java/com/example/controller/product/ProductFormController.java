// src/main/java/com/example/controller/product/ProductFormController.java
package com.example.controller.product;

import com.example.model.Product;
import com.example.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer; // Menggunakan Consumer untuk callback

public class ProductFormController {

    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextField priceField;
    @FXML private TextArea descField;
    @FXML private TextField categoryField;
    @FXML private TextField imageField;

    private final ProductService service = new ProductService();
    private Product product; // Produk yang sedang diedit (null jika membuat baru)
    private Consumer<Product> callback; // Callback untuk ProductListController

    // Metode ini dipanggil dari ProductListController untuk mengisi form
    public void setProduct(Product product) {
        this.product = product; // Mengatur produk yang akan diedit

        if (product != null) {
            // Mode Edit: Isi form dengan data produk yang ada
            formTitle.setText("Edit Product");
            titleField.setText(product.getTitle());
            priceField.setText(String.valueOf(product.getPrice()));
            descField.setText(product.getDescription());
            categoryField.setText(product.getCategory());
            imageField.setText(product.getImage());
        } else {
            // Mode Create: Form kosong
            formTitle.setText("Create Product");
            // Tidak perlu mengisi field, biarkan kosong
        }
    }

    // Metode ini dipanggil dari ProductListController untuk mengatur callback
    public void setCallback(Consumer<Product> callback) {
        this.callback = callback;
    }

    // Metode yang dipanggil saat tombol "Save" ditekan
    @FXML
    public void onSave() { // Menghapus 'throws InterruptedException' dari signature
        try {
            // Validasi input harga
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Harga harus berupa angka yang valid.");
                return;
            }

            // Buat objek Product baru atau gunakan objek yang ada
            Product productToSave = (product == null) ? new Product() : product;

            // Set data dari form ke objek Product
            productToSave.setTitle(titleField.getText());
            productToSave.setPrice(price);
            productToSave.setDescription(descField.getText());
            productToSave.setCategory(categoryField.getText());
            productToSave.setImage(imageField.getText());
            
            // Inisialisasi rating jika produk baru
            if (productToSave.getRating() == null) {
                productToSave.setRating(new Product.Rating());
            }

            Product apiResponseProduct;

            if (product == null) {
                // Jika membuat produk baru
                apiResponseProduct = service.createProduct(productToSave);
                if (apiResponseProduct != null) {
                    productToSave.setId(apiResponseProduct.getId());
                } else {
                    System.err.println("API tidak mengembalikan produk setelah create. ID mungkin tidak valid.");
                    productToSave.setId((int) (Math.random() * 1000000) + 1000); // Fallback ID
                }
            } else {
                // Jika mengedit produk yang sudah ada
                apiResponseProduct = service.updateProduct(productToSave);
            }

            // Tutup window form
            ((Stage) titleField.getScene().getWindow()).close();

            // Panggil callback untuk memperbarui TableView di ProductListController
            if (callback != null) {
                callback.accept(productToSave);
            }

        } catch (IOException e) { // Hanya menangkap IOException
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan produk: " + e.getMessage());
        } catch (Exception e) { // Catch all other exceptions (misalnya NullPointerException)
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Umum", "Terjadi kesalahan saat menyimpan produk: " + e.getMessage());
        }
        // Menghapus 'catch (InterruptedException e)' karena tidak lagi reachable
    }

    @FXML
    public void onCancel() {
        ((Stage) titleField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}