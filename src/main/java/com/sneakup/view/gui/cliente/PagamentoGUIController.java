package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.model.domain.Scarpa;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class PagamentoGUIController {

    @FXML private Button btnLogin, btnAccedi, btnOspite, btnIndietro;
    @FXML private Label lblUser, lblRiepilogo, lblTitoloPagina;
    @FXML private Region barraAnimata;

    // Contenitore Scelta (Opzionale se presente nell'FXML)
    @FXML private HBox containerScelta;

    // Dati navigazione
    private String prevFxml = "/com/sneakup/view/Carrello.fxml";
    private Scarpa scarpaDettaglio;
    private Scarpa prodottoAcquistoSingolo;

    private String prevBrand, prevGen, prevCat, prevRicerca;

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

        // SE L'UTENTE È GIÀ LOGGATO, NON DEVE STARE QUI -> VAI A TIPO CONSEGNA
        if (Sessione.getInstance().isLoggato()) {
            // Usiamo Platform.runLater per attendere che l'interfaccia sia pronta prima di cambiare scena
            javafx.application.Platform.runLater(() -> vaiATipoConsegna(null));
            return;
        }

        // Calcola e mostra i totali iniziali
        aggiornaRiepilogo();
    }

    public void setDatiNavigazione(String prevFxml, Scarpa scarpaDettaglio, Scarpa prodottoAcquisto,
                                   String brand, String gen, String cat, String ricerca) {
        this.prevFxml = prevFxml;
        this.scarpaDettaglio = scarpaDettaglio;
        this.prodottoAcquistoSingolo = prodottoAcquisto;
        this.prevBrand = brand;
        this.prevGen = gen;
        this.prevCat = cat;
        this.prevRicerca = ricerca;

        if (btnIndietro != null && prevFxml != null && prevFxml.contains("DettaglioProdotto")) {
            btnIndietro.setText("← Torna al prodotto");
        }

        aggiornaRiepilogo();
    }

    private void aggiornaRiepilogo() {
        double subtotale = 0.0;
        if (prodottoAcquistoSingolo != null) {
            subtotale = prodottoAcquistoSingolo.getPrezzo();
        } else {
            for (Scarpa s : Sessione.getInstance().getCarrello()) {
                subtotale += s.getPrezzo();
            }
        }

        // Qui siamo ospiti, quindi la spedizione è a pagamento standard per ora (sarà confermata in TipoConsegna)
        double spedizione = 9.99;
        double totaleFinale = subtotale + spedizione;

        if (lblRiepilogo != null) {
            lblRiepilogo.setText(String.format("Totale Articoli: €%.2f  |  Spedizione stimata: €%.2f", subtotale, spedizione));
        }
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(prevFxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof DettaglioProdottoGUIController && scarpaDettaglio != null) {
                DettaglioProdottoGUIController dp = (DettaglioProdottoGUIController) controller;
                dp.setDettagliScarpa(scarpaDettaglio);
                dp.setStatoPrecedente(prevBrand, prevCat, prevGen, prevRicerca);
                dp.setProvenienza("/com/sneakup/view/ListaProdotti.fxml");
            } else if (controller instanceof CarrelloGUIController) {
                ((CarrelloGUIController) controller).setProvenienza(
                        "/com/sneakup/view/ListaProdotti.fxml", prevBrand, prevGen, prevCat, prevRicerca, scarpaDettaglio);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            navigaVerso("/com/sneakup/view/Carrello.fxml", event);
        }
    }

    @FXML
    private void handleAccediEPaga(ActionEvent event) {
        // Vai al Login -> Se successo -> Vai a TipoConsegna
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();
            com.sneakup.view.gui.common.LoginGUIController loginCtrl = loader.getController();

            // Impostiamo TipoConsegna come destinazione post-login
            loginCtrl.setProvenienza("/com/sneakup/view/TipoConsegna.fxml", prevBrand, prevGen, prevCat);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleProcediOspite(ActionEvent event) {
        // Ospite va direttamente alla scelta consegna
        vaiATipoConsegna(event);
    }

    private void vaiATipoConsegna(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/TipoConsegna.fxml"));
            Parent root = loader.load();

            TipoConsegnaGUIController ctrl = loader.getController();
            // Passiamo tutti i dati al controller della consegna
            // Se event è null (chiamata automatica), usiamo un riferimento di default o quello salvato
            ctrl.setDatiNavigazione("/com/sneakup/view/Pagamento.fxml", scarpaDettaglio, prodottoAcquistoSingolo,
                    prevBrand, prevGen, prevCat, prevRicerca);

            // Recuperiamo lo stage: se event è null (es. redirect automatico), usiamo btnOspite o un altro nodo
            Stage stage;
            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else if (btnOspite != null && btnOspite.getScene() != null) {
                stage = (Stage) btnOspite.getScene().getWindow();
            } else {
                return; // Caso limite
            }

            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGAZIONE STANDARD ---
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handlePreferiti(ActionEvent event) { navigaVerso("/com/sneakup/view/Preferiti.fxml", event); }
    @FXML private void handleCarrelloMenu(ActionEvent event) { navigaVerso("/com/sneakup/view/Carrello.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Funzione non disponibile.").showAndWait(); }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage;
            if (e != null && e.getSource() instanceof Node) {
                stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            } else if (btnOspite != null && btnOspite.getScene() != null) {
                stage = (Stage) btnOspite.getScene().getWindow();
            } else { return; }
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Point2D loc = barraAnimata.getParent().sceneToLocal(b.getMinX(), 0); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.05); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
}