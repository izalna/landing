// src/main/java/com/example/controller/product/ProductListController.java
package com.example.controller.product;

import com.example.controller.HomeController;
import com.example.model.Product;
import com.example.service.ProductService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality; // Impor untuk jendela modal
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class ProductListController {

    @FXML
    private VBox productListRoot;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn; // Tipe Integer
    @FXML
    private TableColumn<Product, String> titleColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn; // Tipe Double
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, String> imageColumn;
    @FXML
    private TableColumn<Product, String> ratingRateColumn;
    @FXML
    private TableColumn<Product, String> ratingCountColumn;
    @FXML
    private TableColumn<Product, Void> actionsColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label totalProductsLabel;

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        // Inisialisasi kolom tabel
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        
        // Memperbaiki: Menggunakan PropertyValueFactory untuk kolom Double
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price")); 
        
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        imageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImage()));

        // Untuk rating.rate dan rating.count, tetap pakai SimpleStringProperty karena Anda menggabungkan
        ratingRateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRating().getRate())));
        ratingCountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRating().getCount())));

        // Contoh untuk kolom Actions (Edit/Delete Button)
        actionsColumn.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-background-radius: 5;");

                editButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    handleEditProduct(product); // Memanggil metode edit
                });
                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    try {
                        handleDeleteProduct(product);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } // Memanggil metode delete
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        productTable.setItems(products);
        loadProductData();
    }

    private void loadProductData() {
        try {
            List<Product> products = productService.fetchProducts();
            productTable.getItems().setAll(products);
            if (totalProductsLabel != null) {
                totalProductsLabel.setText(String.valueOf(products.size()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load product data: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduct() {
        // Membuka form untuk menambah produk baru
        openProductForm(null); // Null berarti produk baru
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        try {
            List<Product> allProducts = productService.fetchProducts();
            List<Product> filteredProducts = allProducts.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(query) ||
                                 p.getCategory().toLowerCase().contains(query) ||
                                 p.getDescription().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            productTable.getItems().setAll(filteredProducts);
            if (totalProductsLabel != null) {
                totalProductsLabel.setText(String.valueOf(filteredProducts.size()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to search products: " + e.getMessage());
        }
    }

    private void handleEditProduct(Product product) {
        // Membuka form untuk mengedit produk yang sudah ada
        openProductForm(product); // Mengirim objek produk yang akan diedit
    }

    private void handleDeleteProduct(Product product) throws InterruptedException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Anda yakin ingin menghapus produk '" + product.getTitle() + "'?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    productService.deleteProduct(product.getId());
                    products.remove(product); 
                    showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Produk berhasil dihapus (simulasi).");
                    loadProductData(); // Muat ulang data setelah penghapusan
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus produk: " + e.getMessage());
                }
            }
        });
    }

    // Metode baru untuk membuka form produk (baik untuk tambah maupun edit)
    private void openProductForm(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/product/product-form-view.fxml")); // Pastikan path ini benar!
            Stage formStage = new Stage();
            formStage.setTitle(product == null ? "Create Product" : "Edit Product");
            Scene formScene = new Scene(loader.load());
            formStage.setScene(formScene);
            formStage.initModality(Modality.APPLICATION_MODAL); // Membuat jendela modal

            ProductFormController formController = loader.getController();
            formController.setProduct(product); // Mengatur produk di controller form
            formController.setCallback(savedProduct -> {
                // Callback setelah produk disimpan dari form
                if (savedProduct != null) {
                    // Update the table with the new/updated product
                    // For Fake API, IDs are often generated on client-side for "new" users
                    // or are just echoed for "updated" users.
                    // This logic might need adjustment for a real backend.
                    boolean found = false;
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getId() == savedProduct.getId()) {
                            products.set(i, savedProduct); // Replace existing
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        products.add(savedProduct); // Add new product
                    }
                    productTable.refresh(); // Refresh table to show changes
                    loadProductData(); // Muat ulang data untuk menyinkronkan total produk
                }
            });

            formStage.showAndWait(); // Menunggu hingga form ditutup
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open product form: " + e.getMessage() + "\nPeriksa path FXML: /view/product/product-form-view.fxml");
        }
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
            
            if (loader.getLocation() == null) {
                showAlert(Alert.AlertType.ERROR, "Error Navigasi", "File FXML Home tidak ditemukan di lokasi: /view/home.fxml. Periksa struktur folder.");
                return;
            }

            Scene scene = new Scene(loader.load(), 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/home.css").toExternalForm());

            currentStage.setScene(scene);
            currentStage.setTitle("Fake Store App");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Gagal memuat halaman Home dari Product List: " + e.getMessage());
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