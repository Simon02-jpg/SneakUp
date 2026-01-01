package com.sneakup.view.gui.gestoreVendite;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.db.ScarpaDAOJDBC;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.model.Sessione;
import com.sneakup.view.gui.cliente.VisualizzaCatalogoGUIController; // Assicurati che l'import sia corretto
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
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class BenvenutoGUIController {

    @FXML private Region barraAnimata;
    @FXML private TextField searchField;
    @FXML private Button btnClearSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnLoginTop;

    //private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();

    @FXML
    public void initialize() {
        // Gestione visibilità tasto pulizia ricerca
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean showX = !newValue.trim().isEmpty();
                if (btnClearSearch != null) {
                    btnClearSearch.setVisible(showX);
                    btnClearSearch.setManaged(showX);
                }
            });
        }

        // Controllo Sessione all'avvio
        if (Sessione.getInstance().isLoggato()) {
            aggiornaUIUtenteLoggato();
        }
    }

    private void aggiornaUIUtenteLoggato() {
        if (btnLoginTop != null) {
            btnLoginTop.setText("Ciao, " + Sessione.getInstance().getUsername());
            btnLoginTop.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");
            btnLoginTop.setOnAction(this::apriAreaPersonale);
        }
    }

    // ==========================================
    //          NAVIGAZIONE BRAND (RICHIESTA)
    // ==========================================

    @FXML
    private void handleNike(MouseEvent event) {
        navigaVersoSelezioneBrand(event, "NIKE");
    }

    @FXML
    private void handleAdidas(MouseEvent event) {
        navigaVersoSelezioneBrand(event, "ADIDAS");
    }

    @FXML
    private void handlePuma(MouseEvent event) {
        navigaVersoSelezioneBrand(event, "PUMA");
    }

    private void navigaVersoSelezioneBrand(MouseEvent event, String brand) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/VisualizzaCatalogo.fxml"));
            Parent root = loader.load();

            // Ottieni il controller del catalogo e imposta il brand scelto
            VisualizzaCatalogoGUIController controller = loader.getController();
            if (controller != null) {
                controller.setBrand(brand);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root); // Utilizzo di setRoot per fluidità

        } catch (IOException e) {
            e.printStackTrace();
            mostraInfo("Errore", "Impossibile caricare il catalogo " + brand);
        }
    }

    // ==========================================
    //          LOGICA DI NAVIGAZIONE GENERICA
    // ==========================================

    private void apriAreaPersonale(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/AreaPersonale.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            mostraInfo("Errore", "Impossibile caricare l'Area Personale.");
        }
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReloadHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/Benvenuto.fxml"));
            Stage stage = (Stage) btnLoginTop.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    //          ANIMAZIONI E INTERFACCIA
    // ==========================================

    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        Button source = (Button) event.getSource();
        Bounds buttonBounds = source.localToScene(source.getBoundsInLocal());
        Bounds barParentBounds = barraAnimata.getParent().localToScene(barraAnimata.getParent().getBoundsInLocal());

        double newX = buttonBounds.getMinX() - barParentBounds.getMinX()
                + (source.getWidth() / 2) - (barraAnimata.getWidth() / 2);

        barraAnimata.setOpacity(1.0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), barraAnimata);
        tt.setToX(newX);
        tt.play();
    }

    @FXML
    private void nascondiBarra(MouseEvent event) {
        barraAnimata.setOpacity(0.0);
    }

    @FXML public void iconaEntra(MouseEvent event) { zoom((Node) event.getSource(), 1.15); }
    @FXML public void iconaEsce(MouseEvent event) { zoom((Node) event.getSource(), 1.0); }
    @FXML public void animazioneEntra(MouseEvent event) { zoom((Node) event.getSource(), 1.1); }
    @FXML public void animazioneEsce(MouseEvent event) { zoom((Node) event.getSource(), 1.0); }

    private void zoom(Node node, double factor) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setToX(factor);
        st.setToY(factor);
        st.play();
    }

    // ==========================================
    //          LOGICA RICERCA
    // ==========================================
/*
    @FXML
    private void handleCerca(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) return;

        try {
            List<Scarpa> risultati = scarpaDAO.cercaScarpe(keyword);
            if (risultati.isEmpty()) {
                mostraInfo("Nessun risultato", "Non abbiamo trovato prodotti per: " + keyword);
            } else {
                mostraInfo("Risultati", "Trovati " + risultati.size() + " prodotti.");
            }
        } catch (SneakUpException e) {
            mostraInfo("Errore", "Errore database.");
        }
    }
*/
    @FXML
    private void handlePulisciRicerca(ActionEvent event) {
        searchField.setText("");
    }

    private void mostraInfo(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}