package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.ScarpaDAOJDBC;
import com.sneakup.model.domain.Scarpa;
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
import com.sneakup.view.gui.common.LoginGUIController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();
    private List<Scarpa> listaCompleta = new ArrayList<>();

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
        if (comboOrdina != null) comboOrdina.getItems().addAll("Prezzo Crescente", "Prezzo Decrescente");
        if (txtRicerca != null) txtRicerca.textProperty().addListener((o, oldV, newV) -> eseguiFiltri());
    }

    public void setFiltri(String brand, String categoria, String genere) {
        this.isRicercaGlobale = false;
        this.currentBrand = brand;
        this.currentCategoria = categoria;
        this.currentGenere = genere;
        if (lblBrandTitolo != null) lblBrandTitolo.setText(brand.toUpperCase());
        if (lblCategoriaTitolo != null) lblCategoriaTitolo.setText(categoria.toUpperCase() + " - " + genere.toUpperCase());
        caricaDatiDalDB();
        eseguiFiltri();
    }

    public void setRicercaGlobale(String testo) {
        this.isRicercaGlobale = true;
        try {
            if (lblBrandTitolo != null) lblBrandTitolo.setText("RISULTATI");
            if (lblCategoriaTitolo != null) lblCategoriaTitolo.setText("Ricerca: \"" + testo.toUpperCase() + "\"");
            this.currentBrand = null; this.currentCategoria = null; this.currentGenere = null;
            this.listaCompleta = scarpaDAO.cercaPerNome(testo);
            for (Scarpa s : listaCompleta) {
                if (s.getMockVoto() == 0) s.setMockVoto(new Random().nextInt(3) + 3);
            }
            if (txtRicerca != null) txtRicerca.setText(testo);
            eseguiFiltri();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void caricaDatiDalDB() {
        try {
            List<Scarpa> rawData = scarpaDAO.cercaScarpe(currentBrand);
            listaCompleta.clear();
            for (Scarpa s : rawData) {
                boolean matchCat = s.getCategoria() != null && s.getCategoria().equalsIgnoreCase(currentCategoria);
                boolean matchGen = (currentGenere == null) || (s.getGenere() != null && s.getGenere().equalsIgnoreCase(currentGenere));
                if (matchCat && matchGen) {
                    if (s.getMockVoto() == 0) s.setMockVoto(new Random().nextInt(3) + 3);
                    listaCompleta.add(s);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void eseguiFiltri() {
        if (gridProdotti == null) return;
        gridProdotti.getChildren().clear();
        List<Scarpa> filtrati = new ArrayList<>(listaCompleta);

        if (txtRicerca != null && !txtRicerca.getText().isEmpty()) {
            String testo = txtRicerca.getText().toLowerCase().trim();
            filtrati = filtrati.stream().filter(s -> s.getModello().toLowerCase().contains(testo)).collect(Collectors.toList());
        }

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

        if (chkS4 != null && chkS4.isSelected()) {
            filtrati = filtrati.stream().filter(s -> Math.max(s.getMediaVoti(), s.getMockVoto()) >= 4).collect(Collectors.toList());
        } else if (chkS3 != null && chkS3.isSelected()) {
            filtrati = filtrati.stream().filter(s -> Math.max(s.getMediaVoti(), s.getMockVoto()) >= 3).collect(Collectors.toList());
        }

        if (comboOrdina != null && comboOrdina.getValue() != null) {
            String ordine = comboOrdina.getValue();
            if ("Prezzo Crescente".equals(ordine)) filtrati.sort((s1, s2) -> Double.compare(s1.getPrezzo(), s2.getPrezzo()));
            else if ("Prezzo Decrescente".equals(ordine)) filtrati.sort((s1, s2) -> Double.compare(s2.getPrezzo(), s1.getPrezzo()));
        }

        int col = 0; int row = 0;
        for (Scarpa s : filtrati) {
            gridProdotti.add(creaCardProdottoOrizzontale(s), col, row);
            col++;
            if (col == 2) { col = 0; row++; }
        }
        if (filtrati.isEmpty()) gridProdotti.add(new Label("Nessun prodotto trovato."), 0, 0);
    }

    // --- METODO CREAZIONE CARD (CON ANIMAZIONE E CLICK) ---
    private HBox creaCardProdottoOrizzontale(Scarpa s) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 12; -fx-cursor: hand;");
        card.setPrefHeight(180.0);

        // --- STELLA PREFERITI ---
        Label stella = new Label("★");
        boolean isFav = Sessione.getInstance().isPreferito(s);
        stella.setStyle("-fx-font-size: 30px; -fx-cursor: hand; -fx-text-fill: " + (isFav ? "#ffce00;" : "#cccccc;"));

        // Gestione Click Stella (CONSUMA L'EVENTO per non aprire i dettagli)
        stella.setOnMouseClicked(e -> {
            e.consume();
            if (!Sessione.getInstance().isLoggato()) {
                new Alert(Alert.AlertType.WARNING, "Accedi per aggiungere ai preferiti.").showAndWait();
                return;
            }
            if (Sessione.getInstance().isPreferito(s)) {
                Sessione.getInstance().rimuoviPreferito(s);
                stella.setStyle("-fx-font-size: 30px; -fx-text-fill: #cccccc; -fx-cursor: hand;");
            } else {
                Sessione.getInstance().aggiungiPreferito(s);
                stella.setStyle("-fx-font-size: 30px; -fx-text-fill: #ffce00; -fx-cursor: hand;");
            }
        });

        // --- IMMAGINE ---
        ImageView img = new ImageView();
        try {
            String path = (s.getUrlImmagine() != null) ? s.getUrlImmagine() : "/images/scarpa 1.png";
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) {}
        img.setFitHeight(130); img.setFitWidth(160); img.setPreserveRatio(true);

        // --- INFO ---
        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER_LEFT);
        Label nome = new Label(s.getModello());
        nome.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label desc = new Label(s.getMarca() + " - " + s.getGenere());
        desc.setStyle("-fx-text-fill: gray;");

        HBox stelleBox = new HBox(2);
        int voto = Math.max(s.getMockVoto(), (int)s.getMediaVoti());
        if(voto == 0) voto = 4;
        for(int i=0; i<5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (i < voto ? "#ffce00;" : "#e0e0e0;"));
            stelleBox.getChildren().add(star);
        }
        info.getChildren().addAll(nome, desc, stelleBox);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label prezzo = new Label(String.format("€%.0f", s.getPrezzo()));
        prezzo.setFont(Font.font("System", FontWeight.BOLD, 24));

        card.getChildren().addAll(stella, img, info, prezzo);

        // --- CLICK SULLA CARD (SEMPLIFICATO SENZA ANIMAZIONE PER TEST) ---
        // Se questo funziona, poi rimettiamo l'animazione.
        // L'importante ora è capire perché non cambia pagina.
        card.setOnMouseClicked(e -> {
            // Se l'utente ha cliccato sulla stella, ci ha già pensato l'handler sopra (e.consume)
            // Se arriviamo qui, l'utente ha cliccato sulla parte bianca della card
            apriDettaglioProdotto(s, e);
        });

        return card;
    }

    // --- METODO DI NAVIGAZIONE CON DEBUG AVANZATO ---
    private void apriDettaglioProdotto(Scarpa s, MouseEvent e) {
        try {
            // Controlli di sicurezza...
            if (getClass().getResource("/com/sneakup/view/DettaglioProdotto.fxml") == null) {
                new Alert(Alert.AlertType.ERROR, "File FXML non trovato!").showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/DettaglioProdotto.fxml"));
            Parent root = loader.load();

            DettaglioProdottoGUIController controller = loader.getController();
            controller.setDettagliScarpa(s);

            // === [NOVITÀ] PASSIAMO LO STATO CORRENTE ===
            // Se siamo in ricerca globale, passiamo il testo della ricerca, altrimenti i filtri
            String testoRicerca = isRicercaGlobale ? (txtRicerca != null ? txtRicerca.getText() : "") : null;

            controller.setStatoPrecedente(
                    this.currentBrand,
                    this.currentCategoria,
                    this.currentGenere,
                    testoRicerca
            );
            // ===========================================

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore apertura dettagli: " + ex.getMessage()).showAndWait();
        }
    }

    // --- ALTRI METODI FXML ---
    @FXML public void handleRicercaKey(KeyEvent event) { eseguiFiltri(); }
    @FXML public void handleFiltroAction(ActionEvent event) { eseguiFiltri(); }
    @FXML private void handleIndietro(ActionEvent event) {
        if (isRicercaGlobale) navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
        else {
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

    // Metodo Login Corretto
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

    // --- GESTIONE CARRELLO (HEADER) ---
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController ctrl = loader.getController();

            String ricerca = isRicercaGlobale ? (txtRicerca != null ? txtRicerca.getText() : "") : null;

            // CORREZIONE: Aggiungiamo 'null' come sesto parametro perché non veniamo da un dettaglio scarpa
            ctrl.setProvenienza(
                    "/com/sneakup/view/ListaProdotti.fxml",
                    this.currentBrand,
                    this.currentGenere,
                    this.currentCategoria,
                    ricerca,
                    null  // <--- Questo risolve l'errore!
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- GESTIONE PREFERITI (HEADER) ---
    @FXML
    private void handlePreferiti(ActionEvent event) {
        if(Sessione.getInstance().isLoggato()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Preferiti.fxml"));
                Parent root = loader.load();

                PreferitiGUIController ctrl = loader.getController();

                String ricerca = isRicercaGlobale ? (txtRicerca != null ? txtRicerca.getText() : "") : null;

                // Passiamo la provenienza anche ai preferiti così sanno come tornare qui
                ctrl.setProvenienza(
                        "/com/sneakup/view/ListaProdotti.fxml",
                        this.currentBrand,
                        this.currentGenere,
                        this.currentCategoria,
                        ricerca,
                        null // Nessuna scarpa specifica selezionata
                );

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Accedi per vedere i tuoi preferiti.").showAndWait();
        }
    }

    // --- AREA PERSONALE (HEADER) ---
    @FXML
    private void handleVaiAreaPersonale(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/AreaPersonale.fxml"));
            Parent root = loader.load();

            AreaPersonaleGUIController ctrl = loader.getController();

            // Permettiamo all'Area Personale di sapere che veniamo da questa lista
            ctrl.setProvenienza("/com/sneakup/view/ListaProdotti.fxml", this.currentBrand);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Stato ordine in arrivo").showAndWait(); }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Parent p = barraAnimata.getParent(); Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY()); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
}