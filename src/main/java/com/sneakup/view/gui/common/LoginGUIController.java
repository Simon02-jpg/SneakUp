package com.sneakup.view.gui.common;

import com.sneakup.controller.LoginController; // Aggiunto per delegare la logica
import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import com.sneakup.model.domain.Ruolo; // Aggiunto per gestire i ruoli
import com.sneakup.util.AlertUtils;
import com.sneakup.exception.SneakUpException; // Assicurati sia importato
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginGUIController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Region barraAnimata;

    // Inizializzazione del Controller Applicativo per coerenza con Registrazione e Recupero
    private final LoginController loginController = new LoginController();

    @FXML
    public void initialize() {
        if (barraAnimata != null) {
            barraAnimata.setOpacity(0.0);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String inputEmail = emailField.getText().trim();
        String pwd = passwordField.getText();

        if (inputEmail.isEmpty() || pwd.isEmpty()) {
            AlertUtils.mostraErrore("Inserisci email e password per accedere.");
            return;
        }

        try {
            // Utilizziamo UtenteDAOJDBC per recuperare l'utente dal DB
            UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();
            Utente u = utenteDAO.recuperaDatiUtente(inputEmail);

            // Verifica credenziali confrontando la password inserita con quella nel DB
            if (u != null && u.getPassword().equals(pwd)) {

                // Gestione del ruolo: se lo USERNAME nel DB è 'seller', lo trattiamo come ADMIN
                String ruolo = u.getUsername().equalsIgnoreCase("seller") ? "ADMIN" : "CLIENTE";
                Sessione.getInstance().login(u.getUsername(), ruolo);

                System.out.println("Login effettuato: " + u.getUsername() + " con ruolo " + ruolo);

                // Navigazione differenziata basata sul ruolo
                if (ruolo.equals("ADMIN")) {
                    navigaVerso(event, "/com/sneakup/view/AreaVenditore.fxml");
                } else {
                    navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml");
                }

            } else {
                AlertUtils.mostraErrore("Email o password errati.");
            }

        } catch (SneakUpException e) {
            AlertUtils.mostraErrore("Errore di connessione al Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- METODI DI NAVIGAZIONE ---

    @FXML
    private void handlePasswordDimenticata(ActionEvent event) {
        navigaVerso(event, "/com/sneakup/view/RecuperoPassword.fxml");
    }

    @FXML
    private void handleRegistrazione(ActionEvent event) {
        navigaVerso(event, "/com/sneakup/view/Registrazione.fxml");
    }

    @FXML
    private void handleReloadHome(ActionEvent event) {
        navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml");
    }

    @FXML
    private void handleReloadHome(MouseEvent event) {
        navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml");
    }

    // --- ALTRI GESTORI ---
    @FXML private void handleLoginGoogle(ActionEvent event) { AlertUtils.mostraInfo("Non disponibile."); }
    @FXML private void handleCarrello(ActionEvent event) { AlertUtils.mostraInfo("Accedi prima."); }
    @FXML private void handleStatoOrdine(ActionEvent event) { AlertUtils.mostraInfo("Accedi prima."); }
    @FXML private void handlePreferiti(ActionEvent event) { AlertUtils.mostraInfo("Accedi prima."); }

    // --- ANIMAZIONI ---
    @FXML
    public void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }

    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata != null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void navigaVerso(java.util.EventObject event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore caricamento: " + fxmlPath);
        }
    }
}