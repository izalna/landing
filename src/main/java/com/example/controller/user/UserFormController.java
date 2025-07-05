package com.example.controller.user; // Pastikan package ini benar

import com.example.model.User;
import com.example.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.Consumer; // For callback

public class UserFormController {

    @FXML private Label formTitle;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstnameField;
    @FXML private TextField lastnameField;
    @FXML private TextField phoneField;
    // For address (simplified for form, can be expanded)
    @FXML private TextField cityField;
    @FXML private TextField streetField;
    @FXML private TextField streetNumberField;
    @FXML private TextField zipcodeField;

    private final UserService userService = new UserService();
    private User user; // User being edited (null if creating new)
    private Consumer<User> callback; // Callback to UserListController

    public void setUser(User user) {
        this.user = user;

        if (user != null) {
            formTitle.setText("Edit User");
            emailField.setText(user.getEmail());
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword()); // Note: password usually not returned or should be handled securely

            if (user.getName() != null) {
                firstnameField.setText(user.getName().getFirstname());
                lastnameField.setText(user.getName().getLastname());
            }
            phoneField.setText(user.getPhone());

            if (user.getAddress() != null) {
                cityField.setText(user.getAddress().getCity());
                streetField.setText(user.getAddress().getStreet());
                streetNumberField.setText(String.valueOf(user.getAddress().getNumber()));
                zipcodeField.setText(user.getAddress().getZipcode());
            }
        } else {
            formTitle.setText("Create User");
        }
    }

    public void setCallback(Consumer<User> callback) {
        this.callback = callback;
    }

    @FXML
    public void onSaveUser() {
        try {
            User userToSave = (user == null) ? new User() : user;

            // Handle Name object
            User.Name name = userToSave.getName();
            if (name == null) name = new User.Name();
            name.setFirstname(firstnameField.getText());
            name.setLastname(lastnameField.getText());
            userToSave.setName(name);

            // Handle Address object (simplified, can add GeoLocation)
            User.Address address = userToSave.getAddress();
            if (address == null) address = new User.Address();
            address.setCity(cityField.getText());
            address.setStreet(streetField.getText());
            try {
                address.setNumber(Integer.parseInt(streetNumberField.getText()));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Street number must be a valid integer.");
                return;
            }
            address.setZipcode(zipcodeField.getText());
            userToSave.setAddress(address);

            userToSave.setEmail(emailField.getText());
            userToSave.setUsername(usernameField.getText());
            userToSave.setPassword(passwordField.getText());
            userToSave.setPhone(phoneField.getText());

            User apiResponseUser;

            if (user == null) {
                // For CREATE
                apiResponseUser = userService.createUser(userToSave);
                if (apiResponseUser != null && apiResponseUser.getId() != 0) {
                    userToSave.setId(apiResponseUser.getId());
                } else {
                    // Fallback for Fake API: assign a random ID if API doesn't return one
                    userToSave.setId((int) (Math.random() * 1000000) + 1000);
                }
            } else {
                // For UPDATE
                apiResponseUser = userService.updateUser(userToSave);
            }

            ((Stage) formTitle.getScene().getWindow()).close();
            if (callback != null) {
                callback.accept(userToSave); // Send the updated/new user object back
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save user: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelUser() {
        ((Stage) formTitle.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}