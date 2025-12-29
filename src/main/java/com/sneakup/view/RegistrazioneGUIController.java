package com.sneakup.view;

import com.sneakup.controller.LoginController;
import com.sneakup.model.domain.Ruolo;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class RegistrazioneGUIController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private Region barraAnimata;

    private final LoginController loginController = new LoginController();

    // --- NAVIGAZIONE HEADER ---

    @FXML
    private void handleReloadHome() {
        // Cliccando il Logo o Home, torniamo alla pagina principale (Benvenuto)
        vaiA_Benvenuto();
    }

    private void vaiA_Benvenuto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Benvenuto.fxml"));
            Parent root = loader.load();

            // Usa un campo qualsiasi per trovare lo stage
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));

            // Fix schermo intero
            stage.setMaximized(false);
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        // L'omino o Annulla portano al Login
        tornaAlLogin(event);
    }

    @FXML
    private void tornaAlLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage;
            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) usernameField.getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- ALERT E MENU ---
    @FXML private void handleCarrello(ActionEvent event) { mostraInfo("Info", "Torna alla Home per il carrello."); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraInfo("Info", "Torna alla Home per tracciare ordini."); }
    @FXML private void handlePreferiti(ActionEvent event) { mostraInfo("Info", "Devi accedere per i preferiti."); }

    private void mostraInfo(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    private void mostraAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    // --- ANIMAZIONI HEADER E BOTTONI ---

    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        Button source = (Button) event.getSource();
        Bounds buttonBounds = source.localToScene(source.getBoundsInLocal());
        Bounds barParentBounds = barraAnimata.getParent().localToScene(barraAnimata.getParent().getBoundsInLocal());

        double newX = buttonBounds.getMinX() - barParentBounds.getMinX()
                + (source.getWidth() / 2) - (barraAnimata.getWidth() / 2);

        if (barraAnimata.getOpacity() < 1) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), barraAnimata);
            ft.setToValue(1.0);
            ft.play();
        }

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), barraAnimata);
        tt.setToX(newX);
        tt.play();
    }

    @FXML
    private void nascondiBarra(MouseEvent event) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), barraAnimata);
        ft.setToValue(0.0);
        ft.play();
    }

    @FXML
    private void iconaEntra(MouseEvent event) {
        Node n = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(1.15); st.setToY(1.15); st.play();
    }

    @FXML
    private void iconaEsce(MouseEvent event) {
        Node n = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(1.0); st.setToY(1.0); st.play();
    }

    @FXML
    private void animazioneEntraBottone(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.05); st.setToY(1.05); st.play();
    }

    @FXML
    private void animazioneEsceBottone(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.0); st.setToY(1.0); st.play();
    }

    // --- LOGICA REGISTRAZIONE ---

    @FXML
    private void handleRegistrazione(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String conferma = confermaPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || conferma.isEmpty()) {
            mostraAlert("Errore", "Compila tutti i campi.");
            return;
        }

        if (!password.equals(conferma)) {
            mostraAlert("Errore", "Le password non coincidono.");
            return;
        }

        // --- MODIFICA QUI SOTTO: Ho aggiunto 'email' ---
        boolean successo = loginController.registraUtente(username, password, email, Ruolo.CLIENTE);

        if (successo) {
            mostraInfo("Successo", "Registrazione completata! Ora puoi accedere.");
            tornaAlLogin(event);
        } else {
            mostraAlert("Errore", "Username già esistente.");
        }
    }
}