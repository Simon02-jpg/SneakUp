package com.sneakup.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BenvenutoGUIController {

    // Gestione click sui Loghi
    @FXML
    private void handleNike(ActionEvent event) {
        System.out.println("Scelto brand: Nike");
        vaiAlLogin(event);
    }

    @FXML
    private void handleAdidas(ActionEvent event) {
        System.out.println("Scelto brand: Adidas");
        vaiAlLogin(event);
    }

    @FXML
    private void handlePuma(ActionEvent event) {
        System.out.println("Scelto brand: Puma");
        vaiAlLogin(event);
    }

    // Gestione click sul Menu in alto e Login
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        System.out.println("Navigazione verso Login...");
        vaiAlLogin(event);
    }

    // Metodo helper per cambiare scena
    private void vaiAlLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();

            // Verifica che l'evento non sia nullo (può capitare se chiamato da codice)
            if (event != null && event.getSource() instanceof Node) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true); // Mantiene schermo intero
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRORE CRITICO: Impossibile caricare Login.fxml. Controlla il percorso.");
        }
    }
}