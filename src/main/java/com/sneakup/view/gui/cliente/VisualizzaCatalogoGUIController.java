package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.util.AlertUtils;
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

public class VisualizzaCatalogoGUIController {

    @FXML private Region barraAnimata;
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti, btnLogin;
    @FXML private Label lblUser;

    // AGGIUNTO: Riferimento per cambiare il testo "NIKE" in "ADIDAS" o "PUMA"
    @FXML private Label lblBrandTitolo;

    private String brandSelezionato = "NIKE";

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
     * Metodo chiamato dal BenvenutoGUIController per passare il brand scelto.
     */
    public void setBrand(String brand) {
        this.brandSelezionato = brand;
        // Aggiorna visivamente il titolo nella pagina
        if (lblBrandTitolo != null) {
            lblBrandTitolo.setText(brand.toUpperCase());
        }
    }

    // --- METODI PER IL CATALOGO ---
    @FXML private void handleUomo(ActionEvent event) {
        // Usa la variabile dinamica invece di "NIKE" fisso
        navigaVersoSelezione(event, "Uomo", brandSelezionato);
    }

    @FXML private void handleDonna(ActionEvent event) {
        // Usa la variabile dinamica invece di "NIKE" fisso
        navigaVersoSelezione(event, "Donna", brandSelezionato);
    }

    private void navigaVersoSelezione(ActionEvent event, String genere, String brand) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/SelezioneCategoria.fxml"));
            Parent root = loader.load();
            SelezioneCategoriaGUIController controller = loader.getController();
            if (controller != null) controller.setDati(genere, brand);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- METODI PER LA NAVBAR ---
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml"); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml"); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/Login.fxml"); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso(event, "/com/sneakup/view/AreaPersonale.fxml"); }

    @FXML private void handleCarrello(ActionEvent event) { System.out.println("Carrello"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { System.out.println("Stato Ordine"); }
    @FXML private void handlePreferiti(ActionEvent event) { System.out.println("Preferiti"); }

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
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void navigaVerso(Object event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) ((javafx.event.Event) event).getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
}