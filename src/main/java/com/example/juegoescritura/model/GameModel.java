package com.example.juegoescritura.model;

import java.util.List;
import java.util.Random;

public class GameModel {
    private final List<String> words = List.of(
            "Hola", "Programación", "Java", "Escritura rápida", "Scene Builder",
            "Interfaz gráfica", "JavaFX", "Evento", "Teclado", "Contador",
            "Correcto!", "Incorrecto", "Nivel avanzado", "Puntuación", "Persistencia"
    );
    private final Random random = new Random();

    // parámetros de tiempo/puntuación
    private final int baseTimePerLevel = 20; // segundos iniciales
    private final int reductionPerStep = 2;  // segundos que se reduce por cada step
    private final int pointsPerReduction = 50; // cada X puntos reduce tiempo en reductionPerStep
    private final int minTime = 2; // mínimo segundos permitidos

    public String nextWord() {
        return words.get(random.nextInt(words.size()));
    }

    /**
     * Calcula el tiempo disponible para el nivel en función de la puntuación actual.
     * @param score puntuación actual del jugador
     * @return segundos para el siguiente nivel (>= minTime)
     */
    public int getTimeForScore(int score) {
        if (score <= 0) return baseTimePerLevel;
        int steps = score / pointsPerReduction;
        int newTime = baseTimePerLevel - steps * reductionPerStep;
        if (newTime < minTime) newTime = minTime;
        return newTime;
    }
}




