package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.view.gui.common.LoginGUIController; // IMPORTANTE
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

public class SelezioneCategoriaGUIController {

    @FXML private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Label lblUser, lblGenere, lblBrandTitolo;
    @FXML private Region barraAnimata;

    private String brandSelezionato = "NIKE";
    private String genereSelezionato = "UOMO";

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true);
                lblUser.setManaged(true);
            }
        }
    }

    public void setDati(String genere, String brand) {
        this.brandSelezionato = brand;
        this.genereSelezionato = genere;
        if (lblGenere != null) lblGenere.setText(genere.toUpperCase());
        if (lblBrandTitolo != null) lblBrandTitolo.setText(brand.toUpperCase());
    }

    @FXML
    private void handleCategoria(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String categoriaScelta = btn.getText().replace("\n", " ").trim();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/ListaProdotti.fxml"));
            Parent root = loader.load();
            ListaProdottiGUIController controller = loader.getController();
            controller.setFiltri(this.brandSelezionato, categoriaScelta, this.genereSelezionato);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostraInfo("Errore", "Impossibile aprire la lista prodotti.");
        }
    }

    // --- METODO LOGIN MODIFICATO PER TORNARE INDIETRO CORRETTAMENTE ---
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();

            LoginGUIController loginCtrl = loader.getController();
            // Passiamo Brand e Genere. Categoria è null perché non l'abbiamo ancora scelta.
            loginCtrl.setProvenienza("/com/sneakup/view/SelezioneCategoria.fxml",
                    this.brandSelezionato,
                    this.genereSelezionato,
                    null);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleIndietro(ActionEvent event) { navigaVerso("/com/sneakup/view/VisualizzaCatalogo.fxml", event); }
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleCarrello(ActionEvent event) { mostraInfo("Carrello", "In arrivo"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraInfo("Stato", "In arrivo"); }
    @FXML private void handlePreferiti(ActionEvent event) { mostraInfo("Preferiti", "In arrivo"); }

    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Parent p = barraAnimata.getParent(); Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY()); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node) e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if(fxml.contains("VisualizzaCatalogo")) {
                VisualizzaCatalogoGUIController c = loader.getController();
                c.setBrand(this.brandSelezionato);
            }
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex) { ex.printStackTrace(); }
    }
    private void mostraInfo(String t, String m) { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
}