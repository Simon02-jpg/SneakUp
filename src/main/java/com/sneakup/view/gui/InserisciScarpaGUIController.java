package com.sneakup.view.gui;

import com.sneakup.controller.InserisciScarpaController;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Scarpa;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class InserisciScarpaGUIController {

    @FXML private TextField modelloField;
    @FXML private TextField marcaField;
    @FXML private ComboBox<String> categoriaBox;
    @FXML private TextField tagliaField;
    @FXML private TextField prezzoField;
    @FXML private TextField quantitaField;
    @FXML private Button btnInserisci; // Riferimento al bottone per cambiargli il testo

    private final InserisciScarpaController logicController = new InserisciScarpaController();

    // Variabile per tracciare se siamo in modifica (se null = inserimento nuovo)
    private Scarpa scarpaDaModificare = null;

    @FXML
    public void initialize() {
        categoriaBox.getItems().addAll("Running", "Basket", "Calcio", "Lifestyle", "Tennis");
        categoriaBox.getSelectionModel().selectFirst();
    }

    // METODO NUOVO: Chiamato dalla tabella quando clicchi "Modifica"
    public void setDatiScarpa(Scarpa s) {
        this.scarpaDaModificare = s;

        // Pre-compila i campi
        modelloField.setText(s.getModello());
        marcaField.setText(s.getMarca());
        categoriaBox.setValue(s.getCategoria());
        tagliaField.setText(String.valueOf(s.getTaglia()));
        prezzoField.setText(String.valueOf(s.getPrezzo()));
        quantitaField.setText(String.valueOf(s.getQuantitaDisponibile()));

        // Cambia il testo del bottone
        btnInserisci.setText("SALVA MODIFICHE");
    }

    @FXML
    private void handleInserisci() {
        try {
            String modello = modelloField.getText();
            String marca = marcaField.getText();
            String categoria = categoriaBox.getValue();
            String taglia = tagliaField.getText();
            String prezzo = prezzoField.getText();
            String quantita = quantitaField.getText();

            if (scarpaDaModificare == null) {
                // CASO 1: NUOVO INSERIMENTO
                logicController.inserisciNuovaScarpa(modello, marca, categoria, prezzo, taglia, quantita);
                mostraMessaggio("Successo", "Scarpa aggiunta al catalogo!", Alert.AlertType.INFORMATION);
                pulisciCampi();
            } else {
                // CASO 2: MODIFICA ESISTENTE
                logicController.aggiornaScarpa(scarpaDaModificare.getId(), modello, marca, categoria, prezzo, taglia, quantita);
                mostraMessaggio("Modifica Completata", "La scarpa è stata aggiornata correttamente.", Alert.AlertType.INFORMATION);
                // Chiude la finestra o torna indietro (opzionale, qui puliamo solo)
                tornaAlMenu(null);
            }

        } catch (SneakUpException e) {
            mostraMessaggio("Errore", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostraMessaggio("Errore Critico", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void tornaAlMenu(javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/sneakup/view/HomePrincipale.fxml"));
            javafx.scene.Parent root = loader.load();
            // Se event è null (chiamato da codice), dobbiamo gestire la scene in altro modo, ma per semplicità assumiamo che la finestra sia aperta
            if (modelloField.getScene() != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) modelloField.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.show();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void mostraMessaggio(String titolo, String contenuto, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }

    private void pulisciCampi() {
        modelloField.clear();
        marcaField.clear();
        tagliaField.clear();
        prezzoField.clear();
        quantitaField.clear();
        categoriaBox.getSelectionModel().selectFirst();
        scarpaDaModificare = null;
        btnInserisci.setText("AGGIUNGI AL CATALOGO");
    }
}