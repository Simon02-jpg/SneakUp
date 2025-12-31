package com.sneakup.util;

import javafx.scene.control.Alert;

public class AlertUtils {

    /**
     * Mostra un messaggio di successo (Icona Informazione)
     */
    public static void mostraSuccesso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Mostra un messaggio di errore (Icona Errore)
     */
    public static void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    /**
     * Mostra un messaggio informativo generico (Icona Informazione)
     * Utile per avvisi tipo "Funzionalità in arrivo"
     */
    public static void mostraInfo(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazione"); // Titolo diverso da "Successo"
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}