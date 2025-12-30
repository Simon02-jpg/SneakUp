package com.sneakup.view.gui.gestoreVendite;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.db.ScarpaDAOJDBC;
import com.sneakup.model.domain.Scarpa;
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

    private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();

    @FXML
    public void initialize() {
        // 1. Gestione Ricerca (X che appare/scompare)
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean showX = !newValue.trim().isEmpty();
                if (btnClearSearch != null) {
                    btnClearSearch.setVisible(showX);
                    btnClearSearch.setManaged(showX);
                }
            });
        }

        // 2. CONTROLLO SESSIONE: Se loggato, cambia il tasto Login
        if (Sessione.getInstance().isLoggato()) {
            String nome = Sessione.getInstance().getUsername();

            if (btnLoginTop != null) {
                btnLoginTop.setText("Ciao, " + nome);
                btnLoginTop.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");

                // --- PUNTO CRUCIALE: Apri Area Personale invece di Logout ---
                btnLoginTop.setOnAction(event -> apriAreaPersonale(event));
            }
        }
    }

    // --- NUOVO METODO: Apre la pagina dell'Area Personale ---
    private void apriAreaPersonale(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/AreaPersonale.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

            // Fix Schermo Intero
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostraInfo("Errore", "Impossibile caricare l'Area Personale.");
        }
    }

    // --- METODO DISCONNESSIONE (Usato dall'Area Personale, non qui) ---
    private void handleLogout(ActionEvent event) {
        Sessione.getInstance().logout();
        mostraInfo("Disconnessione", "Logout effettuato con successo.");
        handleReloadHome();
    }

    // ==========================================
    //          LOGICA RICERCA PRODOTTO
    // ==========================================

    @FXML
    private void handleCerca(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            applicaEffettoPressione((Button) event.getSource());
        }

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            mostraInfo("Attenzione", "Inserisci il nome di una scarpa o una marca.");
            return;
        }

        try {
            List<Scarpa> risultati = scarpaDAO.cercaScarpe(keyword);

            if (risultati.isEmpty()) {
                mostraInfo("Nessun risultato", "Non abbiamo trovato scarpe per: " + keyword);
            } else {
                StringBuilder sb = new StringBuilder("Trovati " + risultati.size() + " prodotti:\n");
                for (Scarpa s : risultati) {
                    sb.append("- ").append(s.getMarca()).append(" ").append(s.getModello())
                            .append(" (€").append(s.getPrezzo()).append(")\n");
                }
                mostraInfo("Risultati Ricerca", sb.toString());
            }

        } catch (SneakUpException e) {
            e.printStackTrace();
            mostraInfo("Errore", "Errore di connessione al database.");
        }
    }

    @FXML
    private void handlePulisciRicerca(ActionEvent event) {
        applicaEffettoPressione(btnClearSearch);
        searchField.setText("");
        searchField.requestFocus();
    }

    private void applicaEffettoPressione(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.9);
        st.setToY(0.9);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    // ==========================================
    //          NAVIGAZIONE E MENU
    // ==========================================

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

    // Altre animazioni brand
    @FXML
    private void animazioneEntra(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.1);
        st.setToY(1.1);
        st.play();
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        shadow.setRadius(30);
        shadow.setOffsetY(10);
        btn.setEffect(shadow);
    }

    @FXML
    private void animazioneEsce(MouseEvent event) {
        Button btn = (Button) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(10);
        shadow.setOffsetY(5);
        btn.setEffect(shadow);
    }

    @FXML
    private void handleReloadHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Benvenuto.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) searchField.getScene().getWindow();
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
        mostraInfo("Carrello", "Sezione Carrello (Guest).");
    }

    @FXML
    private void handleStatoOrdine(ActionEvent event) {
        mostraInfo("Stato Ordine", "Traccia la spedizione.");
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        if (Sessione.getInstance().isLoggato()) {
            mostraInfo("Preferiti", "Ecco i tuoi prodotti salvati " + Sessione.getInstance().getUsername());
        } else {
            mostraInfo("Area Riservata", "Effettua il Login per i Preferiti.");
            handleLoginGenerico(event);
        }
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
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

    @FXML private void handleNike(ActionEvent event) { System.out.println("Nike selezionato"); }
    @FXML private void handleAdidas(ActionEvent event) { System.out.println("Adidas selezionato"); }
    @FXML private void handlePuma(ActionEvent event) { System.out.println("Puma selezionato"); }

    private void mostraInfo(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}