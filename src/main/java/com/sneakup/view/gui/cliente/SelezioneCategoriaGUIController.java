package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class SelezioneCategoriaGUIController {

    @FXML private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Label lblUser, lblGenere, lblBrand;
    @FXML private Region barraAnimata;

    private String brandSelezionato;

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
        if (Sessione.getInstance().isLoggato()) {
            btnLogin.setVisible(false);
            btnLogin.setManaged(false);
            lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
            lblUser.setVisible(true);
            lblUser.setManaged(true);
        }
    }

    public void setDati(String genere, String brand) {
        this.brandSelezionato = brand;
        if (lblGenere != null) lblGenere.setText(genere.toUpperCase());
        if (lblBrand != null) lblBrand.setText(brand.toUpperCase());
    }

    @FXML private void handleIndietro(ActionEvent event) { navigaVerso("/com/sneakup/view/VisualizzaCatalogo.fxml", event); }
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }

    @FXML private void handleCarrello(ActionEvent event) { System.out.println("Carrello"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { System.out.println("Stato"); }
    @FXML private void handlePreferiti(ActionEvent event) { System.out.println("Preferiti"); }

    @FXML private void handleCategoria(ActionEvent event) {
        Button b = (Button) event.getSource();
        System.out.println("Filtro: " + brandSelezionato + " - " + b.getText());
    }

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
    @FXML public void nascondiBarra(MouseEvent event) { if (barraAnimata != null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent event) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent event) { lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void navigaVerso(String fxmlPath, java.util.EventObject event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
}