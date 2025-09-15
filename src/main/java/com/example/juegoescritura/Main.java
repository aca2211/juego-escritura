package com.example.juegoescritura;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL menuUrl = Main.class.getResource("/com/example/juegoescritura/menu.fxml");
        if (menuUrl == null) throw new IllegalStateException("No se encontró menu.fxml en resources.");

        FXMLLoader loader = new FXMLLoader(menuUrl);
        Parent root = loader.load();

        // Obtener controlador para pasar stage
        com.example.juegoescritura.controller.MenuController menuController = loader.getController();
        menuController.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root);
        // carga css si existe
        URL css = Main.class.getResource("/com/example/juegoescritura/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        primaryStage.setTitle("Escritura Rápida - Menú");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
