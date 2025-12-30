package com.sneakup.view.gui.gestoreVendite;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuGUIController {

    @FXML
    private void vaiAInserisci(ActionEvent event) {
        cambiaScena(event, "/com/sneakup/view/InserisciScarpa.fxml");
    }

    @FXML
    private void vaiACatalogo(ActionEvent event) {
        cambiaScena(event, "/com/sneakup/view/VisualizzaCatalogo.fxml");
    }

    @FXML
    private void esci(ActionEvent event) {
        System.exit(0);
    }

    // Metodo helper per cambiare pagina senza riscrivere codice
    private void cambiaScena(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Ottieni lo Stage (la finestra) dal bottone cliccato
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}