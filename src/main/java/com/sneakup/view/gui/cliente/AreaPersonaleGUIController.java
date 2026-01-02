package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.view.gui.common.LoginGUIController; // IMPORT AGGIUNTO
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
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class AreaPersonaleGUIController {

    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti;

    // VARIABILI PER LA NAVIGAZIONE "INDIETRO"
    private String paginaPrecedente = "/com/sneakup/view/Benvenuto.fxml";
    private String brandPrecedente = null;

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

        // Gestione Header
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
        // Rimozione focus per estetica
        if (btnHome != null) btnHome.setFocusTraversable(false);
        if (btnCarrello != null) btnCarrello.setFocusTraversable(false);
        if (btnStato != null) btnStato.setFocusTraversable(false);
        if (btnPreferiti != null) btnPreferiti.setFocusTraversable(false);
    }

    public void setProvenienza(String fxml, String brand) {
        this.paginaPrecedente = fxml;
        this.brandPrecedente = brand;
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(paginaPrecedente));
            Parent root = loader.load();

            if (brandPrecedente != null) {
                Object controller = loader.getController();
                if (controller instanceof VisualizzaCatalogoGUIController) {
                    ((VisualizzaCatalogoGUIController) controller).setBrand(brandPrecedente);
                }
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostraInfo("Errore", "Impossibile tornare indietro.");
        }
    }

    // --- ANIMAZIONE BARRA ---
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

    @FXML private void nascondiBarra(MouseEvent event) { if (barraAnimata != null) barraAnimata.setOpacity(0.0); }

    // --- NAVIGAZIONE ---

    // TASTO PREFERITI (AGGIORNATO)
    // --- TROVA QUESTO METODO E SOSTITUISCI IL CONTENUTO ---
    @FXML
    private void handlePreferiti(ActionEvent event) {
        if (Sessione.getInstance().isLoggato()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Preferiti.fxml"));
                Parent root = loader.load();

                PreferitiGUIController ctrl = loader.getController();

                // MODIFICA QUI: Chiamata semplificata che Maven accetta
                ctrl.setProvenienza("/com/sneakup/view/AreaPersonale.fxml");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Accedi per vedere i tuoi preferiti").showAndWait();
        }
    }

    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", null); } // Adattato per MouseEvent

    // LOGIN (AGGIORNATO CON setProvenienza)
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();
            LoginGUIController loginCtrl = loader.getController();
            loginCtrl.setProvenienza("/com/sneakup/view/AreaPersonale.fxml", null, null, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", null); }

    @FXML
    private void handleLogout(ActionEvent event) {
        Sessione.getInstance().logout();
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML private void handleIMieiOrdini(ActionEvent event) { mostraInfo("Storico Ordini", "Funzionalità in arrivo."); }
    @FXML private void handleIMieiDati(ActionEvent event) { navigaVerso("/com/sneakup/view/DatiPersonali.fxml", event); }
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController ctrl = loader.getController();

            // AGGIUNTO IL SESTO PARAMETRO (null) ALLA FINE
            ctrl.setProvenienza(
                    "/com/sneakup/view/AreaPersonale.fxml",
                    null,
                    null,
                    null,
                    null,
                    null  // <--- Questo è il 6° parametro (Scarpa) che mancava
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore Carrello: " + e.getMessage()).showAndWait();
        }
    }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraInfo("Info", "Funzionalità in arrivo."); }

    // --- ANIMAZIONI ---
    @FXML private void sottolineaUser(MouseEvent event) { lblUser.setUnderline(true); }
    @FXML private void ripristinaUser(MouseEvent event) { lblUser.setUnderline(false); }
    @FXML private void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML private void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    @FXML private void animazioneEntraCard(MouseEvent event) { zoom((Node) event.getSource(), 1.05); }
    @FXML private void animazioneEsceCard(MouseEvent event) { zoom((Node) event.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node) e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void navigaVerso(String fxmlPath, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage;
            // Gestione mista ActionEvent / MouseEvent
            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) lblUser.getScene().getWindow();
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void mostraInfo(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}