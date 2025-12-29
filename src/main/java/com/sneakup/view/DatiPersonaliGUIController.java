package com.sneakup.view;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class DatiPersonaliGUIController {

    @FXML private Button btnUserTop;
    @FXML private Region barraAnimata;

    // Campi del form
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField nuovaPasswordField;
    @FXML private TextField indirizzoField;
    @FXML private TextField cittaField;
    @FXML private TextField capField;
    @FXML private TextField cartaField;
    @FXML private TextField scadenzaField;
    @FXML private TextField cvvField;

    private final UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();
    private String passwordAttuale = "";

    @FXML
    public void initialize() {
        if (Sessione.getInstance().isLoggato()) {
            String username = Sessione.getInstance().getUsername();
            btnUserTop.setText("Ciao, " + username);
            usernameField.setText(username);
            caricaDatiDalDB(username);
        }
    }

    private void caricaDatiDalDB(String username) {
        try {
            Utente u = utenteDAO.recuperaDatiUtente(username);
            if (u != null) {
                emailField.setText(u.getEmail());
                passwordAttuale = u.getPassword();
                if (u.getIndirizzo() != null) indirizzoField.setText(u.getIndirizzo());
                if (u.getCitta() != null) cittaField.setText(u.getCitta());
                if (u.getCap() != null) capField.setText(u.getCap());
                if (u.getNumeroCarta() != null) cartaField.setText(u.getNumeroCarta());
                if (u.getScadenzaCarta() != null) scadenzaField.setText(u.getScadenzaCarta());
                if (u.getCvv() != null) cvvField.setText(u.getCvv());
            }
        } catch (SneakUpException e) {
            mostraAlert(Alert.AlertType.ERROR, "Errore DB", "Impossibile caricare i dati.");
        }
    }

    @FXML
    private void handleSalva(ActionEvent event) {
        String passwordFinale = nuovaPasswordField.getText().isEmpty() ? passwordAttuale : nuovaPasswordField.getText();
        Utente utenteAggiornato = new Utente(
                usernameField.getText(), emailField.getText(), passwordFinale,
                indirizzoField.getText(), cittaField.getText(), capField.getText(),
                cartaField.getText(), scadenzaField.getText(), cvvField.getText()
        );

        try {
            utenteDAO.aggiornaUtente(utenteAggiornato);
            mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Dati salvati!");
            passwordAttuale = passwordFinale;
            nuovaPasswordField.clear();
        } catch (SneakUpException e) {
            mostraAlert(Alert.AlertType.ERROR, "Errore Salvataggio", e.getMessage());
        }
    }

    @FXML
    private void handleEliminaProfilo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare il tuo account? È irreversibile.", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Elimina Profilo");
        alert.setHeaderText("ATTENZIONE");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                utenteDAO.eliminaUtente(Sessione.getInstance().getUsername());
                Sessione.getInstance().logout();
                mostraAlert(Alert.AlertType.INFORMATION, "Addio", "Profilo eliminato.");
                handleReloadHome();
            } catch (SneakUpException e) {
                mostraAlert(Alert.AlertType.ERROR, "Errore", e.getMessage());
            }
        }
    }

    // --- NAVIGAZIONE STANDARD ---

    @FXML private void tornaIndietro(ActionEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml"); }
    @FXML private void handleReloadHome() { navigaVerso("/com/sneakup/view/Benvenuto.fxml"); }

    private void navigaVerso(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnUserTop.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false); stage.setMaximized(true);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- ANIMAZIONI HEADER STANDARD ---

    @FXML private void handleCarrello(ActionEvent event) { mostraAlert(Alert.AlertType.INFORMATION, "Info", "Carrello"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraAlert(Alert.AlertType.INFORMATION, "Info", "Stato Ordine"); }

    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        Button source = (Button) event.getSource();
        Bounds buttonBounds = source.localToScene(source.getBoundsInLocal());
        Bounds barParentBounds = barraAnimata.getParent().localToScene(barraAnimata.getParent().getBoundsInLocal());
        double newX = buttonBounds.getMinX() - barParentBounds.getMinX() + (source.getWidth() / 2) - (barraAnimata.getWidth() / 2);

        if (barraAnimata.getOpacity() < 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), barraAnimata);
            ft.setToValue(1.0); ft.play();
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), barraAnimata);
        tt.setToX(newX); tt.play();
    }

    @FXML
    private void nascondiBarra(MouseEvent event) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), barraAnimata);
        ft.setToValue(0.0); ft.play();
    }

    private void mostraAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(text); alert.showAndWait();
    }
}