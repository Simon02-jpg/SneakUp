package com.sneakup.view.gui.gestoreVendite;

import com.sneakup.bean.ScarpaBean; // Import fondamentale del Bean
import com.sneakup.controller.InserisciScarpaController;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.util.AlertUtils; // Import della classe Utility
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class InserisciScarpaGUIController {

    @FXML private TextField modelloField;
    @FXML private TextField marcaField;
    @FXML private ComboBox<String> categoriaBox;
    @FXML private TextField tagliaField;
    @FXML private TextField prezzoField;
    @FXML private TextField quantitaField;
    @FXML private Button btnInserisci;

    private final InserisciScarpaController logicController = new InserisciScarpaController();
    private Scarpa scarpaDaModificare = null;

    @FXML
    public void initialize() {
        categoriaBox.getItems().addAll("Running", "Basket", "Calcio", "Lifestyle", "Tennis");
        categoriaBox.getSelectionModel().selectFirst();
    }

    /**
     * Chiamato dalla tabella quando clicchi "Modifica"
     */
    public void setDatiScarpa(Scarpa s) {
        this.scarpaDaModificare = s;

        // Pre-compila i campi
        modelloField.setText(s.getModello());
        marcaField.setText(s.getMarca());
        categoriaBox.setValue(s.getCategoria());
        tagliaField.setText(String.valueOf(s.getTaglia()));
        prezzoField.setText(String.valueOf(s.getPrezzo()));
        quantitaField.setText(String.valueOf(s.getQuantitaDisponibile()));

        btnInserisci.setText("SALVA MODIFICHE");
    }

    @FXML
    private void handleInserisci() {
        try {
            // 1. CREAZIONE E POPOLAMENTO DEL BEAN
            // (Invece di avere 6 variabili String sparse, usiamo il contenitore)
            ScarpaBean bean = new ScarpaBean();
            bean.setModello(modelloField.getText());
            bean.setMarca(marcaField.getText());
            bean.setCategoria(categoriaBox.getValue());
            bean.setTaglia(tagliaField.getText());
            bean.setPrezzo(prezzoField.getText());
            bean.setQuantita(quantitaField.getText());

            // 2. LOGICA DI INSERIMENTO O AGGIORNAMENTO
            if (scarpaDaModificare == null) {
                // CASO NUOVO: Passiamo solo il bean
                logicController.inserisciNuovaScarpa(bean);
                AlertUtils.mostraSuccesso("Scarpa aggiunta al catalogo con successo!");
                pulisciCampi();
            } else {
                // CASO MODIFICA: Passiamo ID e il bean con i nuovi dati
                logicController.aggiornaScarpa(scarpaDaModificare.getId(), bean);
                AlertUtils.mostraSuccesso("Prodotto aggiornato correttamente.");
                tornaAlMenu(null);
            }

        } catch (SneakUpException e) {
            // Gestione eccezioni di dominio (es. prezzo negativo)
            AlertUtils.mostraErrore(e.getMessage());
        } catch (Exception e) {
            // Gestione errori imprevisti
            AlertUtils.mostraErrore("Errore imprevisto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void tornaAlMenu(ActionEvent event) {
        try {
            // NOTA: Assicurati che il percorso punti a MenuPrincipale.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/MenuPrincipale.fxml"));
            Parent root = loader.load();

            // Recuperiamo lo stage corrente in modo sicuro
            Stage stage;
            if (modelloField.getScene() != null) {
                stage = (Stage) modelloField.getScene().getWindow();
            } else {
                // Fallback se chiamato manualmente (raro)
                return;
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            AlertUtils.mostraErrore("Impossibile caricare il menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void pulisciCampi() {
        modelloField.clear();
        marcaField.clear();
        tagliaField.clear();
        prezzoField.clear();
        quantitaField.clear();
        categoriaBox.getSelectionModel().selectFirst();

        // Reset stato
        scarpaDaModificare = null;
        btnInserisci.setText("AGGIUNGI AL CATALOGO");
    }
}