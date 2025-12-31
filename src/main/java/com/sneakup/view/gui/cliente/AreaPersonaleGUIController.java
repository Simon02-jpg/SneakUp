package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class AreaPersonaleGUIController {

    // --- ELEMENTI HEADER (Sincronizzati con il nuovo FXML) ---
    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;

    // Per compatibilità con i metodi di animazione
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti;

    @FXML
    public void initialize() {
        // 1. Gestione dinamica Header (Login vs Username)
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) {
                btnLogin.setVisible(false);
                btnLogin.setManaged(false);
            }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true);
                lblUser.setManaged(true);
            }
        }

        // 2. Rimuovi il focus dai bottoni (toglie il grigio/azzurro quando apri la pagina)
        btnHome.setFocusTraversable(false);
        btnCarrello.setFocusTraversable(false);
        btnStato.setFocusTraversable(false);
        btnPreferiti.setFocusTraversable(false);
    }

    // --- NAVIGAZIONE NAVBAR ---

    @FXML
    private void handleReloadHome() {
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", null);
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        navigaVerso("/com/sneakup/view/Login.fxml", event);
    }

    @FXML
    private void handleVaiAreaPersonale(MouseEvent event) {
        // Già siamo qui, potresti rinfrescare la pagina o non fare nulla
        navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Sessione.getInstance().logout();
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        navigaVerso("/com/sneakup/view/VisualizzaCatalogo.fxml", event);
    }

    // --- AZIONI SPECIFICHE AREA PERSONALE ---

    @FXML
    private void handleIMieiOrdini(ActionEvent event) {
        mostraInfo("Storico Ordini", "Funzionalità in arrivo.");
    }

    @FXML
    private void handleIMieiDati(ActionEvent event) {
        navigaVerso("/com/sneakup/view/DatiPersonali.fxml", event);
    }

    // --- ANIMAZIONI E INTERFACCIA (Header) ---

    @FXML private void handleCarrello(ActionEvent event) { mostraInfo("Carrello", "Vai al carrello."); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraInfo("Info", "Traccia spedizione."); }

    @FXML
    private void sottolineaUser(MouseEvent event) { lblUser.setUnderline(true); }
    @FXML
    private void ripristinaUser(MouseEvent event) { lblUser.setUnderline(false); }

    @FXML
    private void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML
    private void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Bounds buttonBounds = source.localToScene(source.getBoundsInLocal());
        Bounds barParentBounds = barraAnimata.getParent().localToScene(barraAnimata.getParent().getBoundsInLocal());

        // Calcolo posizione X relativa
        double newX = buttonBounds.getMinX() - barParentBounds.getMinX();

        barraAnimata.setLayoutX(newX);
        barraAnimata.setPrefWidth(buttonBounds.getWidth());
        barraAnimata.setOpacity(1.0);
    }

    @FXML
    private void nascondiBarra(MouseEvent event) {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
    }

    // --- ANIMAZIONI CARD (Contenuto centrale) ---
    @FXML
    private void animazioneEntraCard(MouseEvent event) { zoom((Node) event.getSource(), 1.05); }

    @FXML
    private void animazioneEsceCard(MouseEvent event) { zoom((Node) event.getSource(), 1.0); }

    // --- HELPER NAVIGAZIONE ---
    private void navigaVerso(String fxmlPath, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage;

            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // Fallback se l'evento è nullo (es. chiamato da un metodo interno)
                stage = (Stage) lblUser.getScene().getWindow();
            }

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostraInfo(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}