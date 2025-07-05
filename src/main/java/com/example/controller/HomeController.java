// src/main/java/com/example/controller/HomeController.java
// Perhatikan: Package di sini seharusnya com.example.controller, bukan hanya controller
package com.example.controller;

import com.example.controller.auth.LoginController;
import com.example.controller.cart.CartController;
import com.example.controller.product.ProductListController;
import com.example.controller.user.UserListController;
import com.example.model.Product;
import com.example.model.User;
import com.example.service.ProductService;
import com.example.utilities.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane; // Baris ini perlu diimpor jika digunakan
// import javafx.scene.text.Font; // Tidak perlu diimpor jika tidak digunakan langsung di Java code, hanya di FXML

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent; // Penting untuk event handler

import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML
    private VBox rootVBox; // Ini adalah elemen root dari home.fxml Anda sekarang
    @FXML
    private StackPane landingSection;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private FlowPane productSection;
    @FXML
    private ImageView cartIconImageView;
    @FXML
    private Button manageProductsButton;
    @FXML
    private Button manageUsersButton;
    @FXML
    private Button loginButton;

    private final ProductService productService = new ProductService();
    private CartController cartController;


    @FXML
    public void initialize() {
        // Logika untuk memuat gambar latar belakang
        Image image = new Image(getClass().getResource("/images/landing.jpg").toExternalForm());
        backgroundImage.setImage(image);

        Tooltip cartTooltip = new Tooltip("View Cart");
        Tooltip.install(cartIconImageView, cartTooltip);

        loadProducts();

        updateUIForLoginStatus();
    }

    private void loadProducts() {
        try {
            List<Product> products = productService.fetchProducts();
            productSection.getChildren().clear();
            for (Product product : products) {
                VBox productCard = createProductCard(product);
                productSection.getChildren().add(productCard);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Loading Products", "Failed to load product data: " + e.getMessage());
        }
    }

    public void updateUIForLoginStatus() {
        boolean loggedIn = SessionManager.isLoggedIn();
        User currentUser = SessionManager.getCurrentUser();

        // Pastikan tombol-tombol ini tidak null sebelum diatur visibilitas/managed
        if (manageProductsButton != null) {
            manageProductsButton.setVisible(loggedIn);
            manageProductsButton.setManaged(loggedIn);
        }
        if (manageUsersButton != null) {
            manageUsersButton.setVisible(loggedIn);
            manageUsersButton.setManaged(loggedIn);
        }

        if (cartIconImageView != null) {
            cartIconImageView.setVisible(loggedIn);
            cartIconImageView.setManaged(loggedIn);
        }

        if (loginButton != null) {
            if (loggedIn) {
                String displayName = (currentUser != null && currentUser.getName() != null && currentUser.getName().getFirstname() != null) ?
                                     currentUser.getName().getFirstname() : (currentUser != null ? currentUser.getUsername() : "Guest");
                loginButton.setText("Logout (" + displayName + ")");
                loginButton.setOnAction(event -> handleLogout());
            } else {
                loginButton.setText("Login");
                loginButton.setOnAction(event -> openLoginWindow());
            }
        }
    }

    private void handleLogout() {
        SessionManager.logout();
        showAlert(Alert.AlertType.INFORMATION, "Logout", "Anda telah berhasil logout.");
        updateUIForLoginStatus();
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setStyle(
                "-fx-background-color: #f4f4f4; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        try {
            // Menggunakan URL produk secara langsung
            Image img = new Image(product.getImage(), true);
            imageView.setImage(img);
        } catch (Exception e) {
            System.out.println("Failed to load product image for " + product.getTitle() + " from URL: " + product.getImage() + ". Error: " + e.getMessage());
            // Opsional: set gambar placeholder jika gambar utama gagal dimuat
            // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        }

        Label titleLabel = new Label(product.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label priceLabel = new Label("$" + String.format("%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13px;");

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 5;");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);

        addToCartButton.setOnAction(event -> {
            if (SessionManager.isLoggedIn()) {
                addProductToCartFromHome(product.getId());
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Produk '" + product.getTitle() + "' telah ditambahkan ke keranjang.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Login Diperlukan", "Silakan login terlebih dahulu untuk menambahkan produk ke keranjang.");
                openLoginWindow();
            }
        });

        card.getChildren().addAll(imageView, titleLabel, priceLabel, addToCartButton);
        return card;
    }

    @FXML
    private void goToProductList() {
        if (SessionManager.isLoggedIn()) {
            try {
                // Path FXML untuk product list
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/product/product-list-view.fxml"));
                Scene scene = new Scene(loader.load(), 1200, 800);
                Stage stage = (Stage) rootVBox.getScene().getWindow();
                // Path CSS untuk product list (asumsi product.css di /css/)
                scene.getStylesheets().add(getClass().getResource("/css/product.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Product List");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load product list: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Akses Ditolak", "Anda harus login untuk mengakses halaman ini.");
            openLoginWindow();
        }
    }

    // ... di dalam HomeController.java



    @FXML
    private void goToUserList() {
        if (SessionManager.isLoggedIn()) {
            try {
                // Path FXML untuk user list
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user/user-list-view.fxml"));
                Scene scene = new Scene(loader.load(), 900, 650);
                // Path CSS untuk user list (asumsi users.css di /css/)
                scene.getStylesheets().add(getClass().getResource("/css/users.css").toExternalForm());
                Stage stage = (Stage) rootVBox.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("User Management");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load user list: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Akses Ditolak", "Anda harus login untuk mengakses halaman ini.");
            openLoginWindow();
        }
    }

    @FXML
    public void openLoginWindow() {
        try {
            // Path FXML untuk login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/auth/login-view.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            Scene loginScene = new Scene(loader.load());
            loginStage.setScene(loginScene);
            loginStage.initModality(Modality.APPLICATION_MODAL);

            LoginController loginController = loader.getController();
            loginController.setHomeController(this);

            loginStage.showAndWait();

            updateUIForLoginStatus(); // Perbarui status UI setelah jendela login ditutup

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open login window: " + e.getMessage());
        }
    }

    

    @FXML
private void openCartWindow() {
    if (SessionManager.isLoggedIn()) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cart/cart-view.fxml"));
            Scene scene = new Scene(loader.load());
            CartController cartController = loader.getController();

            // Simpan instance ini ke variabel kelas jika ingin dipakai di addProductToCartFromHome
            this.cartController = cartController;

            Stage stage = new Stage();
            stage.setTitle("Keranjang Belanja");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka keranjang: " + e.getMessage());
        }
    } else {
        showAlert(Alert.AlertType.WARNING, "Login Diperlukan", "Silakan login untuk melihat keranjang.");
        openLoginWindow();
    }
}


   private void addProductToCartFromHome(int productId) {
    if (cartController != null) {
        cartController.addProductToCart(productId, 1);
    } else {
        showAlert(Alert.AlertType.WARNING, "Keranjang Belum Dibuka", "Silakan buka keranjang terlebih dahulu.");
    }
}


    private void showAlert(Alert.AlertType type, String title, String Stringmessage) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(Stringmessage); // Menggunakan parameter yang benar
        alert.showAndWait();
    }

    // Metode ini biasanya dipanggil dari controller lain untuk kembali ke Home
    public void handleBackToHome() { // Ubah aksesibilitas menjadi public agar bisa dipanggil dari luar
        try {
            // Path FXML untuk home
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            // Path CSS untuk home (asumsi home.css di /css/)
            scene.getStylesheets().add(getClass().getResource("/css/home.css").toExternalForm());

            Stage stage = (Stage) rootVBox.getScene().getWindow(); // rootVBox seharusnya sudah tidak null
            stage.setScene(scene);
            stage.setTitle("Fake Store App");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load home view: " + e.getMessage());
        }
    }
}