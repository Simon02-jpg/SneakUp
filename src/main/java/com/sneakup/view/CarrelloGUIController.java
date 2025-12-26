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

    @FXML
    public void initialize() {
        aggiornaVista();
    }

    private void aggiornaVista() {
        listaCarrello.getItems().clear();
        Carrello carrello = Carrello.getInstance();

        for (Scarpa s : carrello.getScarpeSelezionate()) {
            listaCarrello.getItems().add(s.toString());
        }

        totaleLabel.setText("Totale: " + carrello.getTotale() + " €");
    }

    @FXML
    private void confermaOrdine() {
        if (Carrello.getInstance().getScarpeSelezionate().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Il carrello è vuoto!");
            alert.show();
            return;
        }

        // QUI ANDREBBE LA LOGICA DI PAGAMENTO
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ordine Confermato");
        alert.setHeaderText("Grazie per il tuo acquisto!");
        alert.setContentText("Il tuo ordine è stato inviato.");
        alert.showAndWait();

        // Svuota carrello e torna home
        Carrello.getInstance().svuotaCarrello();
        tornaHome(null);
    }

    @FXML
    private void tornaHome(ActionEvent event) {
        try {
            // Se event è null (chiamato da codice), dobbiamo recuperare lo stage in altro modo
            // Ma per semplicità usiamo l'event normale
            if (event != null) {
                Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/HomeCliente.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}