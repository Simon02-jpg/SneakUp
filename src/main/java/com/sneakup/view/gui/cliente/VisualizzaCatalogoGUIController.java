package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.view.gui.common.LoginGUIController;
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

public class VisualizzaCatalogoGUIController {

    @FXML private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Label lblUser, lblBrandTitolo; // Aggiunto lblBrandTitolo
    @FXML private Region barraAnimata;

    private String brandCorrente = "NIKE";

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
    }

    /**
     * Imposta il brand e aggiorna il testo dell'interfaccia
     */
    public void setBrand(String brand) {
        this.brandCorrente = brand;
        if (lblBrandTitolo != null) {
            lblBrandTitolo.setText(brand.toUpperCase()); // Rende il titolo dinamico
        }
    }

    @FXML
    private void handleUomo(ActionEvent event) {
        navigaVersoSelezione(event, "uomo");
    }

    @FXML
    private void handleDonna(ActionEvent event) {
        navigaVersoSelezione(event, "donna");
    }

    private void navigaVersoSelezione(ActionEvent event, String genere) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/SelezioneCategoria.fxml"));
            Parent root = loader.load();

            SelezioneCategoriaGUIController controller = loader.getController();
            controller.setDati(genere, this.brandCorrente);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- CARRELLO (NUOVO: Cliccabile dall'header) ---
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController ctrl = loader.getController();
            // Passiamo il brand corrente (es. NIKE)
            ctrl.setProvenienza("/com/sneakup/view/VisualizzaCatalogo.fxml", this.brandCorrente, null, null, null,null);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();
            LoginGUIController loginCtrl = loader.getController();
            loginCtrl.setProvenienza("/com/sneakup/view/VisualizzaCatalogo.fxml", this.brandCorrente, null, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        if (!Sessione.getInstance().isLoggato()) {
            new Alert(Alert.AlertType.WARNING, "Devi effettuare il login per vedere i preferiti.").showAndWait();
            return;
        }
        navigaVerso("/com/sneakup/view/Preferiti.fxml", event);
    }

    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Servizio in arrivo").showAndWait(); }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node) e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex) { ex.printStackTrace(); }
    }
}