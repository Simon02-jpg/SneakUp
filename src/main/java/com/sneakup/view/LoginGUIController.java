package com.sneakup.view;

import com.sneakup.controller.LoginController;
import com.sneakup.model.domain.Carrello;
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

public class LoginGUIController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Region barraAnimata; // La barra bianca mobile

    private final LoginController loginController = new LoginController();

    // --- NAVIGAZIONE HEADER (Logica uguale a Benvenuto) ---

    @FXML
    private void handleReloadHome() {
        // Se clicco HOME o il LOGO dalla pagina Login, torno alla pagina Benvenuto
        vaiAlBenvenuto(null);
    }

    @FXML
    private void vaiAlBenvenuto(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Benvenuto.fxml"));
            Parent root = loader.load();

            // Trucco per recuperare lo stage anche se event è null (dal click del logo)
            Stage stage = null;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // Se chiamato da un metodo senza evento, usiamo il campo username per trovare la scena
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

    // --- ANIMAZIONI HEADER (Copiato da BenvenutoGUIController) ---

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

    @FXML
    private void handleCarrello(ActionEvent event) {
        mostraAlert("Info", "Accedi per visualizzare il tuo carrello o torna alla Home per il carrello ospite.");
    }

    @FXML
    private void handleStatoOrdine(ActionEvent event) {
        mostraAlert("Info", "Puoi tracciare gli ordini anche dalla Home.");
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        // Sei già nel login
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        // Sei già nel login, magari ricarica la pagina o pulisce i campi
        usernameField.clear();
        passwordField.clear();
    }


    // --- LOGICA LOGIN ESISTENTE ---

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Ruolo ruolo = loginController.verificaLogin(username, password);

            if (ruolo == null) {
                mostraAlert("Errore", "Credenziali errate.");
                return;
            }

            if (ruolo == Ruolo.VENDITORE) {
                cambiaScena(event, "/com/sneakup/view/MenuPrincipale.fxml");
            } else if (ruolo == Ruolo.CLIENTE) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/HomePrincipale.fxml"));
                Parent root = loader.load();

                HomePrincipaleGUIController homeController = loader.getController();
                if (homeController != null) {
                    homeController.setDatiUtente(username);
                    homeController.setCarrello(new Carrello());
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setMaximized(false);
                stage.setMaximized(true);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostraAlert("Errore", "Errore tecnico: " + e.getMessage());
        }
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(false);
        stage.setMaximized(true);
        stage.show();
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void apriRecuperoPassword(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/RecuperoPassword.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void apriRegistrazione(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Registrazione.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void animazioneEntraBottone(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.05); // Zoom leggero del 5%
        st.setToY(1.05);
        st.play();
    }

    @FXML
    private void animazioneEsceBottone(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.0); // Torna normale
        st.setToY(1.0);
        st.play();
    }
}