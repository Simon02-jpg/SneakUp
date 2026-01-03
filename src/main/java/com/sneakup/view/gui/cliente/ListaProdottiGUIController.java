package com.sneakup.view.gui.cliente;

import com.sneakup.controller.GestoreProdotti; // IMPORT NUOVO: Controller Applicativo
import com.sneakup.model.Sessione;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.view.gui.common.LoginGUIController;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaProdottiGUIController {

    @FXML private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Label lblUser, lblBrandTitolo, lblCategoriaTitolo;
    @FXML private Region barraAnimata;

    @FXML private GridPane gridProdotti;
    @FXML private ComboBox<String> comboOrdina;
    @FXML private TextField txtRicerca;
    @FXML private CheckBox chkP1, chkP2, chkP3, chkP4;
    @FXML private CheckBox chkS4, chkS3;

    private String currentBrand;
    private String currentCategoria;
    private String currentGenere;
    private boolean isRicercaGlobale = false;

    // MODIFICA BCE: Sostituito DAO con Gestore
    private final GestoreProdotti gestore = new GestoreProdotti();
    private List<Scarpa> listaCompleta = new ArrayList<>();

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

        // Gestione Utente Loggato
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true);
                lblUser.setManaged(true);
            }
        }

        if (comboOrdina != null) comboOrdina.getItems().addAll("Prezzo Crescente", "Prezzo Decrescente");
        if (txtRicerca != null) txtRicerca.textProperty().addListener((o, oldV, newV) -> eseguiFiltri());
    }

    public void setFiltri(String brand, String categoria, String genere) {
        this.isRicercaGlobale = false;
        this.currentBrand = brand;
        this.currentCategoria = categoria;
        this.currentGenere = genere;

        if (lblBrandTitolo != null) lblBrandTitolo.setText(brand != null ? brand.toUpperCase() : "");
        if (lblCategoriaTitolo != null) lblCategoriaTitolo.setText((categoria != null ? categoria.toUpperCase() : "") + " - " + (genere != null ? genere.toUpperCase() : ""));

        caricaDatiDalDB();
        eseguiFiltri();
    }

    public void setRicercaGlobale(String testo) {
        this.isRicercaGlobale = true;
        try {
            if (lblBrandTitolo != null) lblBrandTitolo.setText("RISULTATI");
            if (lblCategoriaTitolo != null) lblCategoriaTitolo.setText("Ricerca: \"" + testo.toUpperCase() + "\"");

            this.currentBrand = null;
            this.currentCategoria = null;
            this.currentGenere = null;

            // CHIAMATA AL GESTORE
            this.listaCompleta = gestore.ricercaGlobale(testo);

            if (txtRicerca != null) txtRicerca.setText(testo);
            eseguiFiltri();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void caricaDatiDalDB() {
        try {
            // CHIAMATA AL GESTORE: Recupera le scarpe
            List<Scarpa> rawData = gestore.recuperaTutteLeScarpe(currentBrand);

            if (rawData == null) rawData = new ArrayList<>(); // Safety check

            listaCompleta.clear();

            // Filtra in memoria per Categoria e Genere
            for (Scarpa s : rawData) {
                boolean matchCat = (currentCategoria == null) || (s.getCategoria() != null && s.getCategoria().equalsIgnoreCase(currentCategoria));
                boolean matchGen = (currentGenere == null) || (s.getGenere() != null && s.getGenere().equalsIgnoreCase(currentGenere));

                if (matchCat && matchGen) {
                    listaCompleta.add(s);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void eseguiFiltri() {
        if (gridProdotti == null) return;
        gridProdotti.getChildren().clear();

        List<Scarpa> filtrati = new ArrayList<>(listaCompleta);

        // Filtro Ricerca Testuale
        if (txtRicerca != null && !txtRicerca.getText().isEmpty()) {
            String testo = txtRicerca.getText().toLowerCase().trim();
            filtrati = filtrati.stream().filter(s -> s.getModello().toLowerCase().contains(testo)).collect(Collectors.toList());
        }

        // Filtri Prezzo
        if (chkP1 != null && (chkP1.isSelected() || chkP2.isSelected() || chkP3.isSelected() || chkP4.isSelected())) {
            filtrati = filtrati.stream().filter(s -> {
                double p = s.getPrezzo();
                if (chkP1.isSelected() && p <= 50) return true;
                if (chkP2.isSelected() && p > 50 && p <= 100) return true;
                if (chkP3.isSelected() && p > 100 && p <= 200) return true;
                if (chkP4.isSelected() && p > 200) return true;
                return false;
            }).collect(Collectors.toList());
        }

        // --- FILTRI VALUTAZIONE (STELLE REALI DAL GESTORE) ---
        if ((chkS4 != null && chkS4.isSelected()) || (chkS3 != null && chkS3.isSelected())) {
            filtrati = filtrati.stream().filter(s -> {
                // CHIAMATA AL GESTORE
                double mediaReale = gestore.getMediaVoti(s.getId());

                if (chkS4.isSelected() && mediaReale >= 4) return true;
                if (chkS3.isSelected() && mediaReale >= 3) return true;
                return false;
            }).collect(Collectors.toList());
        }

        // Ordinamento
        if (comboOrdina != null && comboOrdina.getValue() != null) {
            String ordine = comboOrdina.getValue();
            if ("Prezzo Crescente".equals(ordine)) filtrati.sort((s1, s2) -> Double.compare(s1.getPrezzo(), s2.getPrezzo()));
            else if ("Prezzo Decrescente".equals(ordine)) filtrati.sort((s1, s2) -> Double.compare(s2.getPrezzo(), s1.getPrezzo()));
        }

        // Creazione Griglia
        int col = 0; int row = 0;
        for (Scarpa s : filtrati) {
            gridProdotti.add(creaCardProdottoOrizzontale(s), col, row);
            col++;
            if (col == 2) { col = 0; row++; }
        }

        if (filtrati.isEmpty()) {
            Label empty = new Label("Nessun prodotto trovato con questi filtri.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-padding: 20;");
            gridProdotti.add(empty, 0, 0);
        }
    }

    // --- METODO CREAZIONE CARD (CON STELLE DAL GESTORE) ---
    private HBox creaCardProdottoOrizzontale(Scarpa s) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 12; -fx-cursor: hand;");
        card.setPrefHeight(180.0);

        // --- STELLA PREFERITI ---
        Label stellaFav = new Label("★");
        boolean isFav = Sessione.getInstance().isPreferito(s);
        stellaFav.setStyle("-fx-font-size: 30px; -fx-cursor: hand; -fx-text-fill: " + (isFav ? "#ffce00;" : "#cccccc;"));

        stellaFav.setOnMouseClicked(e -> {
            e.consume();
            if (!Sessione.getInstance().isLoggato()) {
                new Alert(Alert.AlertType.WARNING, "Accedi per aggiungere ai preferiti.").showAndWait();
                return;
            }
            if (Sessione.getInstance().isPreferito(s)) {
                Sessione.getInstance().rimuoviPreferito(s);
                stellaFav.setStyle("-fx-font-size: 30px; -fx-text-fill: #cccccc; -fx-cursor: hand;");
            } else {
                Sessione.getInstance().aggiungiPreferito(s);
                stellaFav.setStyle("-fx-font-size: 30px; -fx-text-fill: #ffce00; -fx-cursor: hand;");
            }
        });

        // --- IMMAGINE ---
        ImageView img = new ImageView();
        try {
            String path = (s.getUrlImmagine() != null) ? s.getUrlImmagine() : "/images/scarpa 1.png";
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) {}
        img.setFitHeight(130); img.setFitWidth(160); img.setPreserveRatio(true);

        // --- INFO PRODOTTO ---
        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER_LEFT);

        Label nome = new Label(s.getModello());
        nome.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label desc = new Label(s.getMarca() + " - " + s.getGenere());
        desc.setStyle("-fx-text-fill: gray;");

        // === LOGICA STELLE REALE (VIA GESTORE) ===
        // 1. Chiamiamo il GESTORE
        double mediaVoti = gestore.getMediaVoti(s.getId());
        long votoArrotondato = Math.round(mediaVoti);

        HBox stelleBox = new HBox(2);
        for(int i=1; i<=5; i++) {
            Label star = new Label("★");
            // Se l'indice è <= al voto arrotondato, è gialla. Altrimenti grigia.
            String colore = (i <= votoArrotondato) ? "#ffce00;" : "#e0e0e0;";
            star.setStyle("-fx-font-size: 18px; -fx-text-fill: " + colore);
            stelleBox.getChildren().add(star);
        }

        // Numero testuale (opzionale)
        if (mediaVoti > 0) {
            Label lblMedia = new Label(String.format(" (%.1f)", mediaVoti));
            lblMedia.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            stelleBox.getChildren().add(lblMedia);
        }
        // ===========================

        info.getChildren().addAll(nome, desc, stelleBox);
        HBox.setHgrow(info, Priority.ALWAYS);

        // PREZZO: Qui mostriamo il prezzo BASE del DB formattato con i centesimi
        Label prezzo = new Label(String.format("€%.2f", s.getPrezzo()));
        prezzo.setFont(Font.font("System", FontWeight.BOLD, 24));

        card.getChildren().addAll(stellaFav, img, info, prezzo);

        // Click sulla card -> Vai al dettaglio
        card.setOnMouseClicked(e -> {
            if (e.getTarget() != stellaFav) apriDettaglioProdotto(s, e);
        });

        return card;
    }

    private void apriDettaglioProdotto(Scarpa s, MouseEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/DettaglioProdotto.fxml"));
            Parent root = loader.load();

            DettaglioProdottoGUIController controller = loader.getController();
            controller.setDettagliScarpa(s);

            // Passiamo lo stato corrente per poter tornare indietro correttamente
            String testoRicerca = isRicercaGlobale ? (txtRicerca != null ? txtRicerca.getText() : "") : null;
            controller.setStatoPrecedente(this.currentBrand, this.currentCategoria, this.currentGenere, testoRicerca);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore apertura dettagli: " + ex.getMessage()).showAndWait();
        }
    }

    // --- NAVIGAZIONE E BOTTONI ---
    @FXML public void handleRicercaKey(KeyEvent event) { eseguiFiltri(); }
    @FXML public void handleFiltroAction(ActionEvent event) { eseguiFiltri(); }

    @FXML private void handleIndietro(ActionEvent event) {
        if (isRicercaGlobale) {
            navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/SelezioneCategoria.fxml"));
                Parent root = loader.load();
                SelezioneCategoriaGUIController ctrl = loader.getController();
                ctrl.setDati(currentGenere, currentBrand);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }

    @FXML private void handlePreferiti(ActionEvent event) {
        if(Sessione.getInstance().isLoggato()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Preferiti.fxml"));
                Parent root = loader.load();
                PreferitiGUIController ctrl = loader.getController();
                ctrl.setProvenienza("/com/sneakup/view/ListaProdotti.fxml", this.currentBrand, this.currentGenere, this.currentCategoria, isRicercaGlobale ? txtRicerca.getText() : null, null);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            new Alert(Alert.AlertType.WARNING, "Accedi per vedere i tuoi preferiti.").showAndWait();
        }
    }

    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();
            LoginGUIController loginCtrl = loader.getController();
            loginCtrl.setProvenienza("/com/sneakup/view/ListaProdotti.fxml", this.currentBrand, this.currentGenere, this.currentCategoria);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();
            String ricerca = isRicercaGlobale ? (txtRicerca != null ? txtRicerca.getText() : "") : null;
            ctrl.setProvenienza("/com/sneakup/view/ListaProdotti.fxml", this.currentBrand, this.currentGenere, this.currentCategoria, ricerca, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Servizio di tracking non disponibile al momento.").showAndWait(); }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Parent p = barraAnimata.getParent(); Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY()); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
}