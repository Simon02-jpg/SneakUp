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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ListaProdottiGUIController {

    @FXML
    private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML
    private Label lblUser, lblBrandTitolo, lblCategoriaTitolo;
    @FXML
    private Region barraAnimata;

    @FXML
    private GridPane gridProdotti;
    @FXML
    private ComboBox<String> comboOrdina;

    // FILTRI UI
    @FXML
    private TextField txtRicerca;
    @FXML
    private CheckBox chkP1, chkP2, chkP3, chkP4;
    @FXML
    private CheckBox chkS4, chkS3;

    private String currentBrand;
    private String currentCategoria;
    private String currentGenere;

    private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();
    private List<Scarpa> listaCompleta = new ArrayList<>();

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

        // Gestione Utente
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

        // Popola ComboBox ordinamento
        if (comboOrdina != null) comboOrdina.getItems().addAll("Prezzo Crescente", "Prezzo Decrescente");

        // --- CORREZIONE RICERCA LIVE (Live Search) ---
        // Aggiungiamo un "Listener" che ascolta ogni modifica del testo in tempo reale
        if (txtRicerca != null) {
            txtRicerca.textProperty().addListener((observable, oldValue, newValue) -> {
                aggiornaLista(); // Chiama l'aggiornamento ogni volta che il testo cambia
            });
        }
    }

    public void setFiltri(String brand, String categoria, String genere) {
        this.currentBrand = brand;
        this.currentCategoria = categoria;
        this.currentGenere = genere;

        if (lblBrandTitolo != null) lblBrandTitolo.setText(brand.toUpperCase());
        if (lblCategoriaTitolo != null)
            lblCategoriaTitolo.setText(categoria.toUpperCase() + " - " + genere.toUpperCase());

        caricaDatiDalDB();
        aggiornaLista();
    }

    private void caricaDatiDalDB() {
        try {
            List<Scarpa> rawData = scarpaDAO.cercaScarpe(currentBrand);
            listaCompleta.clear();

            for (Scarpa s : rawData) {
                boolean matchCat = s.getCategoria() != null && s.getCategoria().equalsIgnoreCase(currentCategoria);
                boolean matchGen = false;

                // Logica Genere (Uomo/Donna/Unisex)
                if (currentGenere != null && s.getGenere() != null) {
                    if (s.getGenere().equalsIgnoreCase(currentGenere)) matchGen = true;
                } else if (s.getGenere() == null) {
                    matchGen = true;
                }

                if (matchCat && matchGen) {
                    // Se non ci sono recensioni, diamo un voto finto per la demo
                    if (s.getRecensioni().isEmpty()) {
                        s.setMockVoto(new Random().nextInt(3) + 3);
                    }
                    listaCompleta.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aggiornaLista() {
        if (gridProdotti == null) return;
        gridProdotti.getChildren().clear();

        // Partiamo dalla lista completa scaricata dal DB
        List<Scarpa> filtrati = new ArrayList<>(listaCompleta);

        // 1. FILTRO RICERCA LIVE
        // Se c'è testo, teniamo solo le scarpe che contengono quel testo nel modello
        if (txtRicerca != null && !txtRicerca.getText().isEmpty()) {
            String testo = txtRicerca.getText().toLowerCase().trim();
            filtrati = filtrati.stream()
                    .filter(s -> s.getModello().toLowerCase().contains(testo))
                    .collect(Collectors.toList());
        }

        // 2. FILTRO PREZZO
        boolean p1 = chkP1.isSelected();
        boolean p2 = chkP2.isSelected();
        boolean p3 = chkP3.isSelected();
        boolean p4 = chkP4.isSelected();

        if (p1 || p2 || p3 || p4) {
            filtrati = filtrati.stream().filter(s -> {
                double p = s.getPrezzo();
                if (p1 && p >= 0 && p <= 50) return true;
                if (p2 && p > 50 && p <= 100) return true;
                if (p3 && p > 100 && p <= 200) return true;
                if (p4 && p > 200) return true;
                return false;
            }).collect(Collectors.toList());
        }

        // 3. FILTRO STELLE
        if (chkS4.isSelected()) {
            filtrati = filtrati.stream()
                    .filter(s -> Math.max(s.getMediaVoti(), s.getMockVoto()) >= 4)
                    .collect(Collectors.toList());
        } else if (chkS3.isSelected()) {
            filtrati = filtrati.stream()
                    .filter(s -> Math.max(s.getMediaVoti(), s.getMockVoto()) >= 3)
                    .collect(Collectors.toList());
        }

        // 4. ORDINAMENTO
        String ordine = comboOrdina.getValue();
        if ("Prezzo Crescente".equals(ordine))
            filtrati.sort((s1, s2) -> Double.compare(s1.getPrezzo(), s2.getPrezzo()));
        else if ("Prezzo Decrescente".equals(ordine))
            filtrati.sort((s1, s2) -> Double.compare(s2.getPrezzo(), s1.getPrezzo()));

        // RENDERIZZAZIONE
        int col = 0;
        int row = 0;
        for (Scarpa s : filtrati) {
            HBox card = creaCardProdottoOrizzontale(s);
            card.setMaxWidth(Double.MAX_VALUE);
            gridProdotti.add(card, col, row);
            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
        }

        if (filtrati.isEmpty()) {
            Label empty = new Label("Nessun prodotto trovato.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: grey;");
            gridProdotti.add(empty, 0, 0);
        }
    }

    // Questi servono per CheckBox e ComboBox
    @FXML
    public void aggiornaLista(KeyEvent event) {
        aggiornaLista();
    }

    @FXML
    public void aggiornaLista(ActionEvent event) {
        aggiornaLista();
    }

    // --- CARD PRODOTTO (Stile Grande) ---
    private HBox creaCardProdottoOrizzontale(Scarpa s) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: #eeeeee; -fx-border-radius: 12;");
        card.setPrefHeight(180.0);

        // Stella
        Label stella = new Label("★");
        stella.setStyle("-fx-font-size: 30px; -fx-text-fill: #cccccc; -fx-cursor: hand;");
        stella.setOnMouseClicked(e -> {
            if (stella.getStyle().contains("#cccccc"))
                stella.setStyle("-fx-font-size: 30px; -fx-text-fill: #ffce00; -fx-cursor: hand;");
            else stella.setStyle("-fx-font-size: 30px; -fx-text-fill: #cccccc; -fx-cursor: hand;");
            e.consume();
        });

        // Immagine
        ImageView img = new ImageView();
        try {
            String path = (s.getUrlImmagine() != null && !s.getUrlImmagine().isEmpty()) ? s.getUrlImmagine() : "/images/scarpa 1.png";
            if (getClass().getResource(path) != null)
                img.setImage(new Image(getClass().getResource(path).toExternalForm()));
            else img.setImage(new Image(getClass().getResource("/images/logo_nike.png").toExternalForm()));
        } catch (Exception e) {
        }
        img.setFitHeight(130);
        img.setFitWidth(160);
        img.setPreserveRatio(true);

        // Info
        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER_LEFT);
        Label nome = new Label(s.getModello());
        nome.setFont(Font.font("System", FontWeight.BOLD, 22));
        nome.setWrapText(true);
        Label desc = new Label(s.getGenere() + " - " + s.getCategoria());
        desc.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px;");
        info.getChildren().addAll(nome, desc);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Destra
        VBox destra = new VBox(5);
        destra.setAlignment(Pos.CENTER_RIGHT);
        destra.setMinWidth(100);
        Label prezzo = new Label(String.format("€%.0f", s.getPrezzo()));
        prezzo.setFont(Font.font("System", FontWeight.BOLD, 24));

        HBox stelleBox = new HBox(1);
        int voto = (int) Math.max(s.getMediaVoti(), s.getMockVoto());
        if (voto == 0) voto = 0;
        for (int i = 0; i < 5; i++) {
            Label star = new Label("★");
            if (i < voto) star.setStyle("-fx-text-fill: #ffce00; -fx-font-size: 18px;");
            else star.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 18px;");
            stelleBox.getChildren().add(star);
        }
        destra.getChildren().addAll(prezzo, stelleBox);

        card.getChildren().addAll(stella, img, info, destra);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #fcfcfc; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 4); -fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: #cccccc; -fx-border-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 12; -fx-cursor: hand; -fx-border-color: #eeeeee; -fx-border-radius: 12;"));

        return card;
    }

    // --- NAVIGAZIONE ---
    @FXML
    private void handleIndietro(ActionEvent event) {
        navigaVerso("/com/sneakup/view/SelezioneCategoria.fxml", event);
    }

    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        navigaVerso("/com/sneakup/view/Login.fxml", event);
    }

    @FXML
    private void handleReloadHome(ActionEvent event) {
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML
    private void handleReloadHomeMouse(MouseEvent event) {
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML
    private void handleVaiAreaPersonale(MouseEvent event) {
        navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event);
    }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            if (fxml.contains("SelezioneCategoria")) {
                SelezioneCategoriaGUIController c = loader.getController();
                c.setDati(this.currentGenere, this.currentBrand);
            }
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleCarrello(ActionEvent event) {
        mostraInfo("Carrello", "In arrivo");
    }

    @FXML
    private void handleStatoOrdine(ActionEvent event) {
        mostraInfo("Stato", "In arrivo");
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        mostraInfo("Preferiti", "In arrivo");
    }

    @FXML
    public void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }

    @FXML
    public void nascondiBarra(MouseEvent event) {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
    }

    @FXML
    public void sottolineaUser(MouseEvent e) {
        lblUser.setUnderline(true);
    }

    @FXML
    public void ripristinaUser(MouseEvent e) {
        lblUser.setUnderline(false);
    }

    @FXML
    public void iconaEntra(MouseEvent e) {
        zoom((Node) e.getSource(), 1.1);
    }

    @FXML
    public void iconaEsce(MouseEvent e) {
        zoom((Node) e.getSource(), 1.0);
    }

    @FXML
    public void animazioneEntraBottone(MouseEvent e) {
        zoom((Node) e.getSource(), 1.05);
    }

    @FXML
    public void animazioneEsceBottone(MouseEvent e) {
        zoom((Node) e.getSource(), 1.0);
    }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s);
        st.setToY(s);
        st.play();
    }

    private void mostraInfo(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }
}