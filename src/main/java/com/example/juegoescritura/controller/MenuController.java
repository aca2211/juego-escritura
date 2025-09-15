package com.example.juegoescritura.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import com.example.juegoescritura.model.HighScoreManager;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class MenuController {

    @FXML private Label lblHighScore;
    @FXML private Label lblSessionScore;
    @FXML private Button btnStart;
    @FXML private Button btnReset;

    private Stage primaryStage;
    private int sessionScore = 0;
    private final HighScoreManager hsManager = new HighScoreManager();

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        // actualizar pantalla con highscore
        int hs = hsManager.loadHighScore();
        lblHighScore.setText(String.valueOf(hs));
        lblSessionScore.setText(String.valueOf(sessionScore));
    }

    @FXML
    private void onStartClicked() {
        try {
            URL gameUrl = getClass().getResource("/com/example/juegoescritura/game.fxml");
            FXMLLoader loader = new FXMLLoader(gameUrl);
            Parent root = loader.load();

            // obtener controller del juego y pasar callback para fin de partida
            GameController gameController = loader.getController();
            // callback que será llamado por GameController al finalizar la partida
            Consumer<Integer> onGameEnd = finalScore -> {
                sessionScore = 0; // sesión reiniciada cuando termine (puedes cambiar lógica)
                int previousHigh = hsManager.loadHighScore();
                if (finalScore > previousHigh) {
                    hsManager.saveHighScore(finalScore);
                    lblHighScore.setText(String.valueOf(finalScore));
                }
                // volver al menú (UI thread)
                javafx.application.Platform.runLater(() -> {
                    primaryStage.setScene(new Scene(primaryStage.getScene().getRoot())); // placeholder
                    // recargar el menu.fxml para asegurar estado limpio
                    try {
                        FXMLLoader menuLoad = new FXMLLoader(getClass().getResource("/com/example/juegoescritura/menu.fxml"));
                        Parent menuRoot = menuLoad.load();
                        MenuController newMenuController = menuLoad.getController();
                        newMenuController.setPrimaryStage(primaryStage);
                        primaryStage.setScene(new Scene(menuRoot));
                        primaryStage.setTitle("Escritura Rápida - Menú");
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            };

            gameController.setOnGameEnd(onGameEnd);

            // Inicializar escena del juego (puedes pasar score inicial si quieres)
            Scene gameScene = new Scene(root);
            // cargar CSS si existe
            URL css = getClass().getResource("/com/example/juegoescritura/styles.css");
            if (css != null) gameScene.getStylesheets().add(css.toExternalForm());

            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Escritura Rápida - Juego");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onResetHighScore() {
        hsManager.saveHighScore(0);
        lblHighScore.setText("0");
    }
}

