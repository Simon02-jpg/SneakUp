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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class AreaPersonaleGUIController {

    @FXML private Button btnUserTop;
    @FXML private Region barraAnimata;

    @FXML
    public void initialize() {
        if (Sessione.getInstance().isLoggato()) {
            btnUserTop.setText("Ciao, " + Sessione.getInstance().getUsername());
        }
    }

    // --- NAVIGAZIONE STANDARD ---

    @FXML
    private void handleReloadHome() {
        navigaVerso("/com/sneakup/view/Benvenuto.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Sessione.getInstance().logout();
        navigaVerso("/com/sneakup/view/Benvenuto.fxml");
    }

    // --- AZIONI SPECIFICHE PAGINA ---

    @FXML
    private void handleIMieiOrdini(ActionEvent event) {
        mostraInfo("Storico Ordini", "Funzionalità in arrivo.");
    }

    @FXML
    private void handleIMieiDati(ActionEvent event) {
        navigaVerso("/com/sneakup/view/DatiPersonali.fxml");
    }

    // --- ANIMAZIONI HEADER STANDARD ---

    @FXML private void handleCarrello(ActionEvent event) { mostraInfo("Carrello", "Vai al carrello."); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraInfo("Info", "Traccia spedizione."); }

    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        Button source = (Button) event.getSource();
        Bounds buttonBounds = source.localToScene(source.getBoundsInLocal());
        Bounds barParentBounds = barraAnimata.getParent().localToScene(barraAnimata.getParent().getBoundsInLocal());
        double newX = buttonBounds.getMinX() - barParentBounds.getMinX() + (source.getWidth() / 2) - (barraAnimata.getWidth() / 2);

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

    // --- ANIMAZIONI CARD ---
    @FXML
    private void animazioneEntraCard(MouseEvent event) {
        Node nodo = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), nodo);
        st.setToX(1.05); st.setToY(1.05); st.play();
    }

    @FXML
    private void animazioneEsceCard(MouseEvent event) {
        Node nodo = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), nodo);
        st.setToX(1.0); st.setToY(1.0); st.play();
    }

    // Helper
    private void navigaVerso(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnUserTop.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(false);
            stage.setMaximized(true);
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