package com.sneakup.view;

// --- IMPORT DI BASE JAVAFX ---
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog; // Serve per chiedere l'indirizzo
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

// --- IMPORT DEL TUO MODELLO ---
import com.sneakup.model.domain.Carrello;
import com.sneakup.model.domain.Scarpa;

// --- IMPORT PER LA NUOVA LOGICA (Observer & Controller) ---
import com.sneakup.controller.GestioneOrdiniController;
import com.sneakup.pattern.observer.VenditoreNotifiche;
import com.sneakup.exception.SneakUpException;

public class CarrelloGUIController {

    @FXML private ListView<String> listaCarrello;
    @FXML private Label totaleLabel;

    private Carrello carrello;

    @FXML
    public void initialize() {
        // Inizializzazione vuota, il carrello viene passato dopo
    }

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
        aggiornaVista();
    }

    private void aggiornaVista() {
        if (carrello == null) return;

        listaCarrello.getItems().clear();
        for (Scarpa s : carrello.getScarpeSelezionate()) {
            listaCarrello.getItems().add(s.toString());
        }

        // Formattazione a 2 decimali per il prezzo
        totaleLabel.setText(String.format("Totale: € %.2f", carrello.getTotale()));
    }

    @FXML
    private void confermaOrdine() {
        // 1. Controllo se il carrello è vuoto
        if (carrello == null || carrello.getScarpeSelezionate().isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Carrello Vuoto", "Non ci sono articoli da acquistare.");
            return;
        }

        // 2. Chiediamo l'indirizzo di spedizione (Simulazione input utente)
        TextInputDialog dialog = new TextInputDialog("Via Roma 10");
        dialog.setTitle("Dati Spedizione");
        dialog.setHeaderText("Conferma Indirizzo");
        dialog.setContentText("Indirizzo di spedizione:");

        Optional<String> result = dialog.showAndWait();

        // Se l'utente clicca OK e c'è testo
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String indirizzo = result.get();

            try {
                // --- Piattaforma logica & Pattern Observer ---

                // Creiamo il Controller Applicativo (il "Subject")
                GestioneOrdiniController gestioneOrdini = new GestioneOrdiniController();

                // Registriamo l'Observer (il Venditore che riceverà la notifica)
                gestioneOrdini.attach(new VenditoreNotifiche("Nike Store Roma"));

                // Eseguiamo l'acquisto
                gestioneOrdini.effettuaOrdine(this.carrello, indirizzo);

                // --- Successo ---
                mostraAlert(Alert.AlertType.INFORMATION, "Ordine Confermato",
                        "Grazie! Il tuo ordine è stato registrato e il venditore è stato notificato.");

                // Pulizia e ritorno alla home
                carrello.svuotaCarrello();
                tornaHome(null); // Passiamo null se non abbiamo l'evento click diretto

            } catch (SneakUpException e) {
                // Gestione errori di business (es. scarpa finita)
                mostraAlert(Alert.AlertType.ERROR, "Errore durante l'ordine", e.getMessage());
            } catch (Exception e) {
                // Gestione errori imprevisti
                e.printStackTrace();
                mostraAlert(Alert.AlertType.ERROR, "Errore Tecnico", "Si è verificato un errore imprevisto.");
            }
        }
    }

    // Metodo helper per mostrare messaggi
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

            // Se il metodo è chiamato manualmente con null, dobbiamo recuperare lo stage diversamente
            // Qui assumiamo che se event è null, usiamo la finestra corrente di una delle view (es. listaCarrello)
            Stage stage;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) listaCarrello.getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}