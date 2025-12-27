package com.sneakup.pattern.observer;

import com.sneakup.model.domain.Ordine;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VenditoreNotifiche implements Observer {

    private static final Logger logger = Logger.getLogger(VenditoreNotifiche.class.getName());
    private String nomeVenditore;

    public VenditoreNotifiche(String nome) {
        this.nomeVenditore = nome;
    }

    // Nel metodo update di VenditoreNotifiche.java
    @Override
    public void update(Ordine ordine) { // Cambiato da String a Ordine
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Notifica Venditore");
            alert.setHeaderText("Nuovo Ordine Ricevuto!");
            alert.setContentText("Hai ricevuto un nuovo ordine #" + ordine.getId() +
                    "\nTotale: €" + ordine.getTotalePagato());
            alert.show();
        });
    }
}