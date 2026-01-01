package com.sneakup.view.gui.common;

import com.sneakup.model.Sessione;
import com.sneakup.util.AlertUtils;
import com.sneakup.view.gui.cliente.VisualizzaCatalogoGUIController;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Button btnLogin;
    @FXML private Label lblUser;

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

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

        if (btnHome != null) btnHome.setFocusTraversable(false);
        if (btnCarrello != null) btnCarrello.setFocusTraversable(false);
        if (btnStato != null) btnStato.setFocusTraversable(false);
        if (btnPreferiti != null) btnPreferiti.setFocusTraversable(false);

        if (searchField != null && btnClearSearch != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                btnClearSearch.setVisible(newValue != null && !newValue.trim().isEmpty());
            });
        }
    }

    // --- GESTIONE CLICK BRAND ---
    @FXML private void handleNike(ActionEvent event) { navigaVersoCatalogo("NIKE", event); }
    @FXML private void handleAdidas(ActionEvent event) { navigaVersoCatalogo("ADIDAS", event); }
    @FXML private void handlePuma(ActionEvent event) { navigaVersoCatalogo("PUMA", event); }

    private void navigaVersoCatalogo(String brand, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/VisualizzaCatalogo.fxml"));
            Parent root = loader.load();

            VisualizzaCatalogoGUIController controller = loader.getController();
            if (controller != null) {
                controller.setBrand(brand);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // GUARDA LA CONSOLE PER L'ERRORE REALE
            AlertUtils.mostraErrore("Errore caricamento catalogo: " + e.getMessage());
        }
    }

    // --- LOGIN CORRETTO (Compatibile con il nuovo LoginController) ---
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();

            LoginGUIController loginCtrl = loader.getController();

            // Dal Menu generico, non sappiamo esattamente dove siamo (limite attuale).
            // Passiamo null per tornare alla Home, OPPURE implementiamo una logica più complessa.
            // Per ora usiamo null per evitare crash.
            loginCtrl.setProvenienza("/com/sneakup/view/Benvenuto.fxml", null, null, null);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore apertura login.");
        }
    }

    // --- ALTRE NAVIGAZIONI ---
    @FXML private void handleReloadHome(ActionEvent event) { cambiaPagina("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHome(MouseEvent event) { cambiaPagina("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { cambiaPagina("/com/sneakup/view/AreaPersonale.fxml", event); }

    @FXML private void handleCarrello(ActionEvent event) { AlertUtils.mostraInfo("In arrivo"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { AlertUtils.mostraInfo("In arrivo"); }
    @FXML private void handlePreferiti(ActionEvent event) { AlertUtils.mostraInfo("In arrivo"); }
    @FXML private void handleCerca(ActionEvent event) { if (searchField != null) System.out.println("Cerca: " + searchField.getText()); }

    @FXML
    private void handleClearSearch(ActionEvent event) {
        if (searchField != null) {
            searchField.clear();
            searchField.requestFocus();
        }
    }

    private void cambiaPagina(String fxml, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore caricamento: " + fxml);
        }
    }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML public void nascondiBarra(MouseEvent event) { if (barraAnimata != null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    @FXML public void sottolineaUser(MouseEvent event) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent event) { lblUser.setUnderline(false); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }
}