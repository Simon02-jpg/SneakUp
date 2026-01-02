package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.view.gui.common.LoginGUIController;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    // Variabili per mantenere lo stato
    private String brandSelezionato = "NIKE"; // Default
    private String genereSelezionato = "UOMO"; // Default

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

    // Metodo chiamato dalla pagina precedente per impostare i dati
    public void setDati(String genere, String brand) {
        if (brand != null) this.brandSelezionato = brand;
        if (genere != null) this.genereSelezionato = genere;

        if (lblGenere != null) lblGenere.setText(this.genereSelezionato.toUpperCase());
        if (lblBrandTitolo != null) lblBrandTitolo.setText(this.brandSelezionato.toUpperCase());
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
            mostraInfo("Errore nel caricamento della lista prodotti.");
        }
    }

    // --- CARRELLO (CORRETTO) ---
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController ctrl = loader.getController();

            // CORREZIONE FONDAMENTALE:
            // Passiamo il Brand e il Genere attuali invece di null.
            // Così il tasto "Indietro" del carrello saprà ricaricare questa pagina con i dati giusti.
            ctrl.setProvenienza(
                    "/com/sneakup/view/SelezioneCategoria.fxml",
                    this.brandSelezionato,   // Passiamo "NIKE", "ADIDAS", ecc.
                    this.genereSelezionato,  // Passiamo "UOMO", "DONNA"
                    null,                    // Categoria (non ancora selezionata qui)
                    null,                     // Ricerca
                    null
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore Carrello: " + e.getMessage()).showAndWait();
        }
    }

    // --- LOGIN ---
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();
            LoginGUIController loginCtrl = loader.getController();
            loginCtrl.setProvenienza("/com/sneakup/view/SelezioneCategoria.fxml", this.brandSelezionato, this.genereSelezionato, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGAZIONE GENERICA ---
    @FXML
    private void handlePreferiti(ActionEvent event) {
        if (!Sessione.getInstance().isLoggato()) {
            new Alert(Alert.AlertType.WARNING, "Accedi per vedere i preferiti.").showAndWait();
            return;
        }
        navigaVersoGenerico("/com/sneakup/view/Preferiti.fxml", event);
    }

    @FXML private void handleIndietro(ActionEvent event) { navigaVersoGenerico("/com/sneakup/view/VisualizzaCatalogo.fxml", event); }
    @FXML private void handleReloadHome(ActionEvent event) { navigaVersoGenerico("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVersoGenerico("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVersoGenerico("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraInfo("In arrivo"); }

    private void navigaVersoGenerico(String fxml, java.util.EventObject e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Se torniamo alla pagina del Brand (VisualizzaCatalogo), reimpostiamo il brand corretto
            if(fxml.contains("VisualizzaCatalogo")) {
                VisualizzaCatalogoGUIController c = loader.getController();
                c.setBrand(this.brandSelezionato);
            }

            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex) { ex.printStackTrace(); }
    }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Parent p = barraAnimata.getParent();
        javafx.geometry.Point2D loc = p.sceneToLocal(source.localToScene(source.getBoundsInLocal()).getMinX(), source.localToScene(source.getBoundsInLocal()).getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(source.getBoundsInLocal().getWidth());
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

    private void mostraInfo(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }
}