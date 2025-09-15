package com.example.juegoescritura.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class HighScoreManager {
    private final Path file = Path.of(System.getProperty("user.home"), ".typing_highscore.txt");

    public int loadHighScore() {
        try {
            if (Files.exists(file)) {
                String s = Files.readString(file).trim();
                return Integer.parseInt(s);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public void saveHighScore(int score) {
        try {
            Files.writeString(file, String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

