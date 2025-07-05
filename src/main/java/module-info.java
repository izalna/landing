module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http; // Assuming you use HttpClient
    requires com.google.gson; // Assuming you use Gson
    requires okhttp3; // Assuming you use OkHttp

    // Packages containing FXML files or controllers that are loaded by FXML
    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.controller.product to javafx.fxml;
    opens com.example.controller.user to javafx.fxml;
    opens com.example.controller.auth to javafx.fxml;
    opens com.example.controller.cart to javafx.fxml;

    // Package containing model classes that might be introspected by libraries like Gson
    opens com.example.model to com.google.gson; // This is good for Gson

    // Packages that you want to be visible and usable by other modules (if any)
    exports com.example;
    exports com.example.service;
    exports com.example.controller; // Export the base controller package as well
    exports com.example.controller.product;
    exports com.example.controller.user;
    exports com.example.controller.auth;
    exports com.example.controller.cart;
    exports com.example.model; // Export model juga, meski opens lebih krusial untuk refleksi
    exports com.example.utilities;
    // You typically don't export 'model' package unless other modules directly manipulate your model objects
    // exports com.example.model; // Only if truly needed by other modules
}