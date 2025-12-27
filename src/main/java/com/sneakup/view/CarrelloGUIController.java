package com.sneakup.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;
import java.util.List; // FONDAMENTALE

// IMPORT MODELLO E DOMAIN
import com.sneakup.model.domain.Carrello;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.model.domain.Ordine;

// IMPORT LOGICA E PERSISTENZA
import com.sneakup.model.dao.OrdineDAO;
import com.sneakup.model.dao.db.OrdineDAOJDBC;
import com.sneakup.exception.SneakUpException;
import com.sneakup.pattern.observer.VenditoreNotifiche;

public class CarrelloGUIController {

    @FXML private ListView<String> listaCarrello;
    @FXML private Label totaleLabel;

    private Carrello carrello;

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
        aggiornaVista();
    }

    private void aggiornaVista() {
        if (carrello == null) return;
        listaCarrello.getItems().clear();
        for (Scarpa s : carrello.getScarpeSelezionate()) {
            listaCarrello.getItems().add(s.getMarca() + " " + s.getModello() + " - €" + s.getPrezzo());
        }
        totaleLabel.setText(String.format("Totale: € %.2f", carrello.getTotale()));
    }

    @FXML
    private void handleConfermaOrdine(ActionEvent event) {
        // 1. Controllo carrello vuoto
        if (carrello == null || carrello.getScarpeSelezionate().isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione", "Il carrello è vuoto!");
            return;
        }

        // 2. Richiesta indirizzo tramite Dialog
        TextInputDialog dialog = new TextInputDialog("Via Roma, 10");
        dialog.setTitle("Conferma Acquisto");
        dialog.setHeaderText("Inserisci l'indirizzo di spedizione");
        dialog.setContentText("Indirizzo:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String indirizzo = result.get();

            try {
                // 3. Creazione oggetto Ordine (usando il tuo costruttore specifico)
                // Costruttore: Ordine(id, listaScarpe, totale, indirizzo)
                List<Scarpa> scarpeDaComprare = carrello.getScarpe();
                double totale = carrello.getTotale();

                Ordine nuovoOrdine = new Ordine(0, scarpeDaComprare, totale, indirizzo);

                // 4. Salvataggio su Database tramite DAO
                OrdineDAO ordineDAO = new OrdineDAOJDBC();
                ordineDAO.salvaOrdine(nuovoOrdine);

                // 5. Gestione Notifica (Observer)
                // Registriamo il venditore come osservatore per questo acquisto
                VenditoreNotifiche notificatore = new VenditoreNotifiche("Store Centrale");
                carrello.attach(notificatore);

                // 6. Svuota il carrello e NOTIFICA (passando l'ordine creato)
                carrello.svuota(nuovoOrdine);

                // Rimuoviamo l'osservatore dopo la notifica per pulizia
                carrello.detach(notificatore);

                mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Ordine effettuato! Il venditore è stato notificato.");

                // Torna alla home
                tornaHome(event);

            } catch (SneakUpException e) {
                mostraAlert(Alert.AlertType.ERROR, "Errore DB", e.getMessage());
            }
        }
    }

    private void mostraAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void tornaHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/HomeCliente.fxml"));
            Parent root = loader.load();

            // Passiamo il carrello (ora vuoto) alla home
            HomeClienteGUIController controller = loader.getController();
            controller.setCarrello(this.carrello);

            Stage stage = (Stage) listaCarrello.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}