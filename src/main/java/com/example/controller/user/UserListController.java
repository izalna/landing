// src/main/java/com/example/controller/user/UserListController.java

package com.example.controller.user;

import com.example.controller.HomeController; // Jika diperlukan untuk kembali
import com.example.model.User;
import com.example.service.UserService;
import com.example.utilities.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality; // Untuk jendela modal
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox; // Penting untuk event handler

import java.io.IOException;
import java.util.List;

public class UserListController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> addressColumn;
    @FXML
    private TableColumn<User, Void> colActions; // Tambahkan kolom aksi
    @FXML
    private Button backButton; // Ini jika Anda punya tombol 'Back' di FXML

    private ObservableList<User> users = FXCollections.observableArrayList();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Setup table columns
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        nameColumn.setCellValueFactory(cellData -> {
            User.Name name = cellData.getValue().getName();
            return new SimpleStringProperty(name != null ? name.toString() : "N/A");
        });
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        addressColumn.setCellValueFactory(cellData -> {
            User.Address address = cellData.getValue().getAddress();
            return new SimpleStringProperty(address != null ? address.toString() : "N/A");
        });

        // Setup Actions Column (Add Edit and Delete buttons)
        colActions.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                pane.setSpacing(5);
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");

                editButton.setOnAction(event -> {
                    User userToEdit = getTableView().getItems().get(getIndex());
                    openUserForm(userToEdit);
                });

                deleteButton.setOnAction(event -> {
                    User userToDelete = getTableView().getItems().get(getIndex());
                    handleDeleteUser(userToDelete);
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

        userTable.setItems(users);
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> fetchedUsers = userService.fetchAllUsers();
            users.setAll(fetchedUsers);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
        }
    }

    // Metode yang akan dipanggil oleh FXML saat tombol "Create New User" ditekan
    @FXML
    private void onCreateUser(ActionEvent event) { // Perhatikan EventAction parameter
        openUserForm(null); // Passing null indicates a new user
    }

    // Metode untuk membuka form user (untuk create atau edit)
    private void openUserForm(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user/user-form-view.fxml")); // Pastikan path ini benar!
            Stage formStage = new Stage();
            formStage.setTitle(user == null ? "Create User" : "Edit User");
            Scene formScene = new Scene(loader.load());
            formStage.setScene(formScene);
            formStage.initModality(Modality.APPLICATION_MODAL);

            UserFormController formController = loader.getController();
            formController.setUser(user);
            formController.setCallback(savedUser -> {
                // Callback setelah user disimpan dari form
                if (savedUser != null) {
                    // Update the table with the new/updated user
                    // For Fake API, IDs are often generated on client-side for "new" users
                    // or are just echoed for "updated" users.
                    // This logic might need adjustment for a real backend.
                    boolean found = false;
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getId() == savedUser.getId()) {
                            users.set(i, savedUser); // Replace existing
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        users.add(savedUser); // Add new user
                    }
                    userTable.refresh(); // Refresh table to show changes
                }
            });

            formStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open user form: " + e.getMessage());
        }
    }

    // Metode untuk Delete User (dipanggil dari tombol Delete di dalam TableView)
    private void handleDeleteUser(User userToDelete) {
        if (userToDelete != null) {
            try {
                userService.deleteUser(userToDelete.getId());
                users.remove(userToDelete);
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted (simulated): " + userToDelete.getUsername());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml")); // Pastikan path ini benar!
            Scene scene = new Scene(loader.load(), 1200, 800);
            Stage stage = (Stage) userTable.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/css/product.css").toExternalForm()); // Pastikan path CSS ini benar!
            stage.setScene(scene);
            stage.setTitle("Retail Application");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load home view: " + e.getMessage());
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