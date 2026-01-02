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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DettaglioProdottoGUIController {

    @FXML private ImageView imgScarpa;
    @FXML private Label lblModello, lblPrezzo, lblInfoExtra;
    @FXML private Text txtDescrizione;
    @FXML private ComboBox<String> comboTaglia, comboColore;
    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;

    private Scarpa scarpaCorrente;
    private String prevBrand, prevCategoria, prevGenere, prevRicerca;

    /**
     * Riceve i dati dalla ListaProdotti per poter tornare indietro correttamente
     */
    public void setStatoPrecedente(String brand, String cat, String gen, String ricerca) {
        this.prevBrand = brand;
        this.prevCategoria = cat;
        this.prevGenere = gen;
        this.prevRicerca = ricerca;
    }

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

        // Gestione Header Utente
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true); lblUser.setManaged(true);
            }
        }

        // Popolamento combo box (Taglie e Colori di default)
        if (comboTaglia != null && comboTaglia.getItems().isEmpty()) {
            comboTaglia.getItems().addAll("38", "39", "40", "41", "42", "43", "44");
        }
        if (comboColore != null && comboColore.getItems().isEmpty()) {
            comboColore.getItems().addAll("Standard", "Limited Edition", "White/Black");
        }
    }

    /**
     * Popola l'interfaccia con i dati della scarpa selezionata
     */
    public void setDettagliScarpa(Scarpa s) {
        this.scarpaCorrente = s;
        if (lblModello != null) lblModello.setText(s.getModello());
        if (lblPrezzo != null) lblPrezzo.setText(String.format("€%.2f", s.getPrezzo()));

        if (txtDescrizione != null) {
            txtDescrizione.setText((s.getDescrizione() != null && !s.getDescrizione().isEmpty())
                    ? s.getDescrizione() : "Descrizione tecnica non disponibile per questo modello.");
        }

        if (imgScarpa != null && s.getUrlImmagine() != null) {
            try {
                imgScarpa.setImage(new Image(getClass().getResource(s.getUrlImmagine()).toExternalForm()));
            } catch (Exception e) {
                System.err.println("Immagine non caricata: " + s.getUrlImmagine());
            }
        }
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/ListaProdotti.fxml"));
            Parent root = loader.load();
            ListaProdottiGUIController ctrl = loader.getController();

            // Ripristina la lista esattamente come era prima (filtri o ricerca globale)
            if (prevRicerca != null && !prevRicerca.isEmpty()) {
                ctrl.setRicercaGlobale(prevRicerca);
            } else {
                ctrl.setFiltri(prevBrand, prevCategoria, prevGenere);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAggiungiAlCarrello(ActionEvent event) {
        if (scarpaCorrente != null) {
            Sessione.getInstance().aggiungiAlCarrello(scarpaCorrente);
            new Alert(Alert.AlertType.INFORMATION, "Prodotto aggiunto al carrello!").showAndWait();
        }
    }

    @FXML
    private void handleAcquista(ActionEvent event) {
        new Alert(Alert.AlertType.INFORMATION, "Procedendo all'acquisto rapido...").showAndWait();
    }

    @FXML
    private void handleVaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();

            // Passiamo 6 parametri inclusa la scarpaCorrente per permettere il ritorno perfetto
            ctrl.setProvenienza(
                    "/com/sneakup/view/DettaglioProdotto.fxml",
                    prevBrand,
                    prevGenere,
                    prevCategoria,
                    prevRicerca,
                    scarpaCorrente
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        if (Sessione.getInstance().isLoggato()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Preferiti.fxml"));
                Parent root = loader.load();
                PreferitiGUIController ctrl = loader.getController();

                // Passiamo 6 parametri inclusa la scarpaCorrente
                ctrl.setProvenienza(
                        "/com/sneakup/view/DettaglioProdotto.fxml",
                        prevBrand,
                        prevGenere,
                        prevCategoria,
                        prevRicerca,
                        scarpaCorrente
                );

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            new Alert(Alert.AlertType.WARNING, "Accedi per salvare questa scarpa nei preferiti!").showAndWait();
        }
    }

    // --- ANIMAZIONI E HEADER ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Point2D loc = barraAnimata.getParent().sceneToLocal(b.getMinX(), 0);
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }

    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void navigaSemplice(String fxml, java.util.EventObject e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
            s.setScene(new Scene(root));
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    @FXML private void handleReloadHome(ActionEvent event) { navigaSemplice("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaSemplice("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaSemplice("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaSemplice("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) {
        new Alert(Alert.AlertType.INFORMATION, "Il servizio di tracking non è momentaneamente disponibile.").showAndWait();
    }
}