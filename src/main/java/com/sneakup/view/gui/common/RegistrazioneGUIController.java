package com.sneakup.view.gui.common;

import com.sneakup.controller.GestoreUtenti; // IMPORT GESTORE (BCE)
import com.sneakup.model.domain.Utente;      // IMPORT MODEL
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
    @FXML private Button btnHome;

    // MODIFICA BCE: Usiamo il GestoreUtenti
    private final GestoreUtenti gestore = new GestoreUtenti();

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

        // 1. Validazione UI (Feedback immediato)
        if (u.isEmpty() || e.isEmpty() || p.isEmpty()) {
            AlertUtils.mostraErrore("Compila tutti i campi obbligatori.");
            return;
        }

        if (!p.equals(c)) {
            AlertUtils.mostraErrore("Le password non coincidono.");
            return;
        }

        // 2. Creazione Oggetto Utente (Entity)
        Utente nuovoUtente = new Utente();
        nuovoUtente.setUsername(u);
        nuovoUtente.setEmail(e);
        nuovoUtente.setPassword(p);
        // Gli altri campi (indirizzo, carta, ecc.) rimangono null/vuoti per ora

        // 3. Chiamata al Gestore (Control)
        boolean successo = gestore.registraUtente(nuovoUtente);

        if (successo) {
            AlertUtils.mostraSuccesso("Registrazione avvenuta con successo nel database! Ora puoi accedere.");
            tornaAlLogin(event);
        } else {
            // Messaggio generico, ma il DAO stampa l'errore in console (es. duplicate key)
            AlertUtils.mostraErrore("Impossibile registrare l'utente.\nUsername o Email potrebbero essere già presenti,\noppure la password è troppo breve.");
        }
    }

    // --- NAVIGAZIONE HOME ---

    @FXML private void handleReloadHomeMouse(MouseEvent event) { vaiAlBenvenuto(event); }
    @FXML private void handleReloadHome(ActionEvent event) { vaiAlBenvenuto(event); }

    // --- TASTO ANNULLA ---
    @FXML
    private void handleAnnulla(ActionEvent event) {
        tornaAlLogin(event);
    }

    // Alias per compatibilità
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

            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/Registrazione.fxml", null, null, null, null, null);

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
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore navigazione: " + e.getMessage());
        }
    }
}