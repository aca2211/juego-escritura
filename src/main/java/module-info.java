module com.example.juegoescritura {
    requires javafx.controls;
    requires javafx.fxml;

    // Permitir que FXMLLoader use reflexión para instanciar controladores FXML
    opens com.example.juegoescritura.controller to javafx.fxml;

    // Permitir que el lanzador de JavaFX (javafx.graphics) construya la Application Main
    // y además dar acceso reflexivo a FXMLLoader si fuese necesario.
    opens com.example.juegoescritura to javafx.graphics, javafx.fxml;
}

