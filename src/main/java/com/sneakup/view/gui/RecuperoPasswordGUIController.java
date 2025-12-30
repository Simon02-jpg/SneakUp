package com.sneakup.view.gui;

import com.sneakup.controller.LoginController;
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

public class RecuperoPasswordGUIController {

    @FXML private TextField usernameField;
    @FXML private PasswordField nuovaPasswordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private Region barraAnimata;

    private final LoginController loginController = new LoginController();

    // --- NAVIGAZIONE CORRETTA: LOGO/HOME -> BENVENUTO ---

    @FXML
    private void handleReloadHome() {
        try {
            // CARICA LA SCHERMATA BENVENUTO (Home Principale)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Benvenuto.fxml"));
            Parent root = loader.load();

            // Recupera lo stage usando un elemento della scena (es. usernameField)
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

    // --- ALTRI METODI DI NAVIGAZIONE ---

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        tornaAlLogin(event); // L'icona omino invece va giustamente al Login
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

    @FXML
    private void handleCarrello(ActionEvent event) {
        mostraInfo("Info", "Torna alla Home per accedere al carrello.");
    }

    @FXML
    private void handleStatoOrdine(ActionEvent event) {
        mostraInfo("Info", "Torna alla Home per tracciare un ordine.");
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        mostraInfo("Info", "Devi accedere per vedere i preferiti.");
    }

    // --- ANIMAZIONI HEADER ---

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
        Node nodo = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), nodo);
        st.setToX(1.15);
        st.setToY(1.15);
        st.play();
    }

    @FXML
    private void iconaEsce(MouseEvent event) {
        Node nodo = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), nodo);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    // --- ANIMAZIONI BOTTONI PAGINA ---

    @FXML
    private void animazioneEntraBottone(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.05);
        st.setToY(1.05);
        st.play();
    }

    @FXML
    private void animazioneEsceBottone(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    // --- LOGICA CAMBIO PASSWORD ---

    @FXML
    private void handleCambiaPassword(ActionEvent event) {
        String user = usernameField.getText();
        String pass = nuovaPasswordField.getText();
        String conf = confermaPasswordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            mostraAlert("Errore", "Inserisci username e password.");
            return;
        }
        if (!pass.equals(conf)) {
            mostraAlert("Errore", "Le password non coincidono!");
            return;
        }

        boolean rigaAggiornata = loginController.resetPassword(user, pass);

        if (rigaAggiornata) {
            mostraInfo("Successo", "Password aggiornata correttamente!");
            tornaAlLogin(event);
        } else {
            mostraAlert("Errore", "Username non trovato.");
        }
    }

    private void mostraAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    private void mostraInfo(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}