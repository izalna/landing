package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home.fxml"));
        
        // Ambil ukuran layar
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = screenBounds.getWidth();
        double height = screenBounds.getHeight();

        Scene scene = new Scene(loader.load(), width, height); // Ukuran mengikuti layar

        // Tambahkan CSS
        scene.getStylesheets().add(getClass().getResource("/css/home.css").toExternalForm());

        // Set window
        stage.setTitle("Home Page");
        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
