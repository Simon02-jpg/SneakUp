package com.sneakup.view.gui.common;

import com.sneakup.controller.LoginController;
import com.sneakup.model.domain.Ruolo;
import com.sneakup.util.AlertUtils;
import com.sneakup.view.gui.cliente.CarrelloGUIController;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;

import java.io.IOException;

public class RegistrazioneGUIController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private Region barraAnimata;
    @FXML private Button btnHome;

    private final LoginController loginController = new LoginController();

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
    }

    @FXML
    private void handleRegistrazione(ActionEvent event) {
        String u = usernameField.getText().trim();
        String e = emailField.getText().trim();
        String p = passwordField.getText();
        String c = confermaPasswordField.getText();

        // ... controlli sui campi vuoti e password uguali ...

        // Ora 'successo' dipenderà davvero dal database
        boolean successo = loginController.registraUtente(u, p, e, Ruolo.CLIENTE);

        if (successo) {
            AlertUtils.mostraSuccesso("Registrazione avvenuta con successo nel database!");
            tornaAlLogin(event);
        } else {
            // Se arrivi qui, ora sai che l'inserimento è fallito davvero
            AlertUtils.mostraErrore("Impossibile registrare l'utente. Username o Email potrebbero essere già presenti.");
        }
    }

    // --- NAVIGAZIONE HOME (Come Benvenuto.fxml) ---
    @FXML private void handleReloadHome(MouseEvent event) { vaiAlBenvenuto(event); }
    @FXML private void handleReloadHome(ActionEvent event) { vaiAlBenvenuto(event); }

    // --- TASTO ANNULLA (Cruciale) ---
    @FXML
    private void handleAnnulla(ActionEvent event) {
        tornaAlLogin(event);
    }

    // Alias se usi un altro nome
    @FXML private void tornaIndietro(ActionEvent event) { tornaAlLogin(event); }

    // Metodo generico ritorno al Login
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        cambiaPagina("/com/sneakup/view/Login.fxml", event);
    }

    // Per compatibilità con onAction nel FXML
    @FXML private void tornaAlLogin(ActionEvent event) { cambiaPagina("/com/sneakup/view/Login.fxml", event); }

    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            // Passiamo la provenienza di default (Benvenuto)
            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/Registrazione.fxml", null, null, null, null,null);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore Carrello: " + e.getMessage()).showAndWait();
        }
    }

    @FXML private void handleStatoOrdine(ActionEvent event) { AlertUtils.mostraInfo("Non disponibile."); }
    @FXML private void handlePreferiti(ActionEvent event) { AlertUtils.mostraInfo("Non disponibile."); }

    // --- ANIMAZIONI ---
    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML private void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML private void animazioneEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.1); }
    @FXML private void animazioneEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML private void animazioneEntraBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.05); }
    @FXML private void animazioneEsceBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML private void iconaEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.2); }
    @FXML private void iconaEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }
    private void vaiAlBenvenuto(java.util.EventObject event) {
        cambiaPagina("/com/sneakup/view/Benvenuto.fxml", event);
    }
    private void cambiaPagina(String fxml, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = null;
            if (event != null && event.getSource() instanceof Node) stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            else if (usernameField.getScene() != null) stage = (Stage) usernameField.getScene().getWindow();
            if (stage != null) {
                boolean max = stage.isMaximized();
                stage.setScene(new Scene(root));
                stage.setMaximized(max);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore navigazione: " + e.getMessage());
        }
    }

    private void navigaVerso(String fxmlPath, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}