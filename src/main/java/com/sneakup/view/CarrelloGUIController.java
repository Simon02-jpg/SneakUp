package com.sneakup.view;

import com.sneakup.model.domain.Carrello;
import com.sneakup.model.domain.Scarpa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class CarrelloGUIController {

    @FXML private ListView<String> listaCarrello;
    @FXML private Label totaleLabel;

    // 1. Aggiungi questo campo per contenere l'istanza specifica
    private Carrello carrello;

    @FXML
    public void initialize() {
        // Non chiamare aggiornaVista() qui, perché 'carrello' è ancora null!
        // Lo chiameremo dentro setCarrello.
    }

    // 2. Metodo per "iniettare" il carrello da fuori
    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
        aggiornaVista(); // Aggiorna la grafica appena riceviamo i dati
    }

    private void aggiornaVista() {
        // Controllo di sicurezza
        if (carrello == null) return;

        listaCarrello.getItems().clear();

        // 3. Usa 'this.carrello' invece di Carrello.getInstance()
        for (Scarpa s : carrello.getScarpeSelezionate()) {
            listaCarrello.getItems().add(s.toString());
        }

        totaleLabel.setText("Totale: " + carrello.getTotale() + " €");
    }

    @FXML
    private void confermaOrdine() {
        // Controllo null e vuoto
        if (carrello == null || carrello.getScarpeSelezionate().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Il carrello è vuoto o non inizializzato!");
            alert.show();
            return;
        }

        // ... Logica pagamento ...
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ordine Confermato");
        alert.setHeaderText("Grazie per il tuo acquisto!");
        alert.setContentText("Il tuo ordine è stato inviato.");
        alert.showAndWait();

        // 4. Svuota il carrello specifico
        carrello.svuotaCarrello();
        tornaHome(null);
    }

    @FXML
    private void tornaHome(ActionEvent event) {
        try {
            if (event != null) {
                // ATTENZIONE: Qui stai ricaricando la Home da zero.
                // Idealmente dovresti passare il 'carrello' indietro se serve,
                // oppure la Home dovrebbe essere mantenuta in memoria.
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/HomeCliente.fxml"));
                Parent root = loader.load();

                // Opzionale: Se la Home ha bisogno del carrello, glielo ripassi qui
                // HomeClienteGUIController homeCtrl = loader.getController();
                // homeCtrl.setCarrello(this.carrello);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}