package com.sneakup.view.gui.common;

import com.sneakup.model.Sessione; // Importato per gestire lo stato dell'utente
import com.sneakup.util.AlertUtils;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label; // Aggiunto per mostrare il nome utente
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;

import java.io.IOException;

public class MenuGUIController {

    @FXML private TextField searchField;
    @FXML private Button btnClearSearch;
    @FXML private Region barraAnimata;

    // Dichiarazione pulsanti per gestire il focus e la navigazione
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti;

    // Riferimenti per la gestione dinamica del Login
    @FXML private Button btnLogin;
    @FXML private Label lblUser;

    @FXML
    public void initialize() {
        // Nascondi la barra di selezione inizialmente
        if (barraAnimata != null) {
            barraAnimata.setOpacity(0.0);
        }

        // --- LOGICA AGGIORNAMENTO INTERFACCIA BASATA SU SESSIONE ---
        // Verifica se c'è un utente loggato
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) {
                btnLogin.setVisible(false);
                btnLogin.setManaged(false); // Rimuove lo spazio occupato dal pulsante
            }
            if (lblUser != null) {
                // Imposta il messaggio di benvenuto con lo username dalla sessione
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true);
                lblUser.setManaged(true);
            }
        }

        // Toglie il focus automatico per evitare l'effetto selezione sui tasti
        if (btnHome != null) btnHome.setFocusTraversable(false);
        if (btnCarrello != null) btnCarrello.setFocusTraversable(false);
        if (btnStato != null) btnStato.setFocusTraversable(false);
        if (btnPreferiti != null) btnPreferiti.setFocusTraversable(false);

        // Gestione visibilità tasto "X" nel campo ricerca
        if (searchField != null && btnClearSearch != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                btnClearSearch.setVisible(newValue != null && !newValue.trim().isEmpty());
            });
        }
    }

    // --- LOGICA DI NAVIGAZIONE ---

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        cambiaPagina("/com/sneakup/view/Login.fxml", event);
    }

    @FXML
    private void handleReloadHome(ActionEvent event) {
        cambiaPagina("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML
    private void handleReloadHome(MouseEvent event) {
        // Gestisce il click sul logo
        cambiaPagina("/com/sneakup/view/Benvenuto.fxml", event);
    }

    // --- METODI NAVBAR E ANIMAZIONI ---

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

    @FXML
    public void nascondiBarra(MouseEvent event) {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
    }

    @FXML
    public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }

    @FXML
    public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s);
        st.setToY(s);
        st.play();
    }

    // --- AZIONI ---

    @FXML private void handleCarrello(ActionEvent event) { AlertUtils.mostraInfo("Carrello in fase di sviluppo"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { AlertUtils.mostraInfo("Stato Ordine non disponibile"); }
    @FXML private void handlePreferiti(ActionEvent event) { AlertUtils.mostraInfo("Preferiti non disponibili"); }

    @FXML
    private void handleCerca(ActionEvent event) {
        if (searchField != null) System.out.println("Cerca: " + searchField.getText());
    }

    @FXML
    private void handleClearSearch(ActionEvent event) {
        if (searchField != null) {
            searchField.clear();
            searchField.requestFocus();
        }
    }

    @FXML private void handleNike(ActionEvent event) { cambiaPagina("/com/sneakup/view/VisualizzaCatalogo.fxml", event); }
    @FXML private void handleAdidas(ActionEvent event) { cambiaPagina("/com/sneakup/view/VisualizzaCatalogo.fxml", event); }
    @FXML private void handlePuma(ActionEvent event) { cambiaPagina("/com/sneakup/view/VisualizzaCatalogo.fxml", event); }

    // --- HELPER NAVIGAZIONE (Risolve errori di compatibilità EventObject) ---

    private void cambiaPagina(String fxml, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Cast dell'evento per recuperare lo Stage corrente
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show(); // Forza il refresh della finestra
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore nel caricamento della pagina: " + fxml);
        }
    }

    // Metodo per navigare all'Area Personale al click sul nome
    @FXML
    private void handleVaiAreaPersonale(MouseEvent event) {
        // Carica la pagina dell'area personale
        cambiaPagina("/com/sneakup/view/AreaPersonale.fxml", event);
    }

    // Effetto hover: sottolinea il nome quando il mouse entra
    @FXML
    private void sottolineaUser(MouseEvent event) {
        lblUser.setUnderline(true);
        lblUser.setOpacity(0.8);
    }

    // Rimuove l'effetto quando il mouse esce
    @FXML
    private void ripristinaUser(MouseEvent event) {
        lblUser.setUnderline(false);
        lblUser.setOpacity(1.0);
    }
}