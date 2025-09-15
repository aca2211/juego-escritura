package com.example.juegoescritura.controller;

import com.example.juegoescritura.model.GameModel;
import com.example.juegoescritura.model.HighScoreManager;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class GameController {

    @FXML private Label lblWord;
    @FXML private TextField txtInput;
    @FXML private Label lblTimer;
    @FXML private Label lblLevel;
    @FXML private Label lblFeedback;
    @FXML private Button btnValidate;
    @FXML private Button btnRestart;
    @FXML private ProgressBar progressBar;
    @FXML private Label lblScore;

    private final GameModel model = new GameModel();
    private Timeline timeline;
    private int secondsLeft;
    private String currentWord = "";
    private int level = 1;
    private int score = 0;

    // Stage pasado desde MenuController
    private Stage primaryStage;

    // callback opcional que MenuController puede proveer
    private Consumer<Integer> onGameEnd;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setOnGameEnd(Consumer<Integer> onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    @FXML
    private void initialize() {
        btnValidate.setOnAction(e -> validateInput());
        btnRestart.setOnAction(e -> endGameEarly());

        txtInput.setOnKeyPressed((KeyEvent evt) -> {
            if (evt.getCode() == KeyCode.ENTER) {
                validateInput();
                evt.consume();
            }
        });

        lblFeedback.setText("");
        progressBar.setProgress(1.0);
        startNewLevel();
    }

    private void startNewLevel() {
        stopTimer();
        currentWord = model.nextWord();
        lblWord.setText(currentWord);
        txtInput.setText("");
        txtInput.requestFocus();

        secondsLeft = model.getTimeForScore(score);
        lblLevel.setText("Nivel: " + level);
        lblTimer.setText("Tiempo: " + secondsLeft + "s");
        lblScore.setText(String.valueOf(score));
        progressBar.setProgress(1.0);
        startTimer();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            lblTimer.setText("Tiempo: " + secondsLeft + "s");
            double total = model.getTimeForScore(score);
            progressBar.setProgress(1.0 * secondsLeft / total);
            if (secondsLeft <= 0) {
                if (txtInput.getText().equals(currentWord)) {
                    handleSuccess();
                } else {
                    handleFail("Tiempo agotado");
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private void validateInput() {
        String typed = txtInput.getText();
        if (typed == null || typed.isEmpty()) {
            handleFail("No ingresaste nada");
            return;
        }
        if (typed.equals(currentWord)) {
            handleSuccess();
        } else {
            handleFail("Palabra incorrecta");
        }
    }

    private void handleSuccess() {
        stopTimer();
        level++;
        score += 10; // puntos por palabra correcta
        lblFeedback.setText("¡Correcto! +10 puntos");
        startNewLevel();
    }

    private void handleFail(String reason) {
        stopTimer();
        lblFeedback.setText("Fallaste: " + reason);

        // Mostrar alerta no bloqueante
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partida finalizada");
        alert.setHeaderText("Perdiste");
        alert.setContentText("Puntuación final: " + score + "\nMotivo: " + reason);

        // Si tenemos ventana, usarla como owner (mejor UX)
        if (primaryStage != null && primaryStage.getScene() != null) {
            alert.initOwner(primaryStage);
        }
        alert.show();

        // Esperar 1.5-2 segundos, cerrar el alert y volver al menú
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(evt -> {
            try {
                alert.close();
            } catch (Exception ignored) {}
            // Notificar callback (guardar highscore, etc.)
            if (onGameEnd != null) {
                try { onGameEnd.accept(score); } catch (Exception ignored) {}
            }
            // Volver al menú (se hace en la UI thread)
            returnToMenu();
        });
        pause.play();
    }

    private void endGameEarly() {
        handleFail("Reiniciado por usuario");
    }

    private void returnToMenu() {
        // Si no se pasó el primaryStage, no podemos navegar; en ese caso salimos
        if (primaryStage == null) {
            System.err.println("PrimaryStage no establecido en GameController; no se puede volver al menú.");
            return;
        }

        Platform.runLater(() -> {
            try {
                URL menuUrl = getClass().getResource("/com/example/juegoescritura/menu.fxml");
                FXMLLoader loader = new FXMLLoader(menuUrl);
                Parent menuRoot = loader.load();

                // Configurar nuevo controlador del menú con el mismo stage
                MenuController menuController = loader.getController();
                menuController.setPrimaryStage(primaryStage);

                Scene menuScene = new Scene(menuRoot);
                // cargar css si existe
                URL css = getClass().getResource("/com/example/juegoescritura/styles.css");
                if (css != null) menuScene.getStylesheets().add(css.toExternalForm());

                primaryStage.setScene(menuScene);
                primaryStage.setTitle("Escritura Rápida - Menú");
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

