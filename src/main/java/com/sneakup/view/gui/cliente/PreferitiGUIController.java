package com.sneakup.view.gui.cliente;

import com.sneakup.controller.GestoreProdotti;
import com.sneakup.model.Sessione;
import com.sneakup.model.domain.Scarpa;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class PreferitiGUIController {

    @FXML private VBox containerPreferiti;
    @FXML private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;

    private String fxmlPrecedente = "/com/sneakup/view/Benvenuto.fxml";
    private String prevBrand, prevGenere, prevCategoria, prevRicerca;
    private Scarpa scarpaPrecedente;

    private final GestoreProdotti gestore = new GestoreProdotti();

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
        updateHeader();
        caricaPreferiti();
    }

    private void updateHeader() {
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true); lblUser.setManaged(true);
            }
        }
    }

    public void setProvenienza(String fxml) {
        this.fxmlPrecedente = fxml;
    }

    public void setProvenienza(String fxml, String brand, String genere, String categoria, String ricerca, Scarpa scarpa) {
        this.fxmlPrecedente = fxml;
        this.prevBrand = brand;
        this.prevGenere = genere;
        this.prevCategoria = categoria;
        this.prevRicerca = ricerca;
        this.scarpaPrecedente = scarpa;
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPrecedente));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof ListaProdottiGUIController) {
                ListaProdottiGUIController lp = (ListaProdottiGUIController) controller;
                if (prevRicerca != null) lp.setRicercaGlobale(prevRicerca);
                else lp.setFiltri(prevBrand, prevCategoria, prevGenere);
            }
            else if (controller instanceof DettaglioProdottoGUIController) {
                DettaglioProdottoGUIController dp = (DettaglioProdottoGUIController) controller;
                if (scarpaPrecedente != null) dp.setDettagliScarpa(scarpaPrecedente);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
        }
    }

    private void caricaPreferiti() {
        if (containerPreferiti == null) return;
        containerPreferiti.getChildren().clear();

        List<Scarpa> preferiti = Sessione.getInstance().getPreferiti();

        if (preferiti.isEmpty()) {
            Label empty = new Label("La tua lista dei preferiti è vuota.");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: gray; -fx-padding: 20;");
            containerPreferiti.getChildren().add(empty);
            return;
        }

        for (Scarpa s : preferiti) {
            Scarpa sDB = gestore.recuperaScarpaPerId(s.getId());
            if (sDB != null) {
                containerPreferiti.getChildren().add(creaCard(s, sDB));
            }
        }
    }

    private HBox creaCard(Scarpa sPreferita, Scarpa sDB) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 12;");

        // --- 1. SINISTRA: Stella + Immagine ---
        HBox leftGroup = new HBox(15);
        leftGroup.setAlignment(Pos.CENTER_LEFT);

        Label stella = new Label("★");
        stella.setStyle("-fx-font-size: 45px; -fx-text-fill: #ffce00; -fx-cursor: hand;");
        stella.setOnMouseClicked(e -> {
            Sessione.getInstance().rimuoviPreferito(sPreferita);
            caricaPreferiti();
        });

        ImageView img = new ImageView();
        try {
            String path = (sDB.getUrlImmagine() != null) ? sDB.getUrlImmagine() : "/images/scarpa 1.png";
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) { }
        img.setFitHeight(130); img.setFitWidth(130); img.setPreserveRatio(true);
        img.setCursor(javafx.scene.Cursor.HAND);
        img.setOnMouseClicked(e -> animazioneTitoloEOpen(sDB, img));

        leftGroup.getChildren().addAll(stella, img);

        // --- 2. CENTRO: Info e Selettori ---
        VBox centerBox = new VBox(8);
        centerBox.setAlignment(Pos.CENTER_LEFT);

        Label nome = new Label(sDB.getModello());
        nome.setFont(Font.font("System", FontWeight.BOLD, 22));
        nome.setStyle("-fx-text-fill: #ff0000; -fx-cursor: hand;");
        nome.setOnMouseClicked(e -> animazioneTitoloEOpen(sDB, nome));

        double mediaVoti = gestore.getMediaVoti(sDB.getId());
        HBox ratingBox = new HBox(2);
        for(int i=1; i<=5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (i <= Math.round(mediaVoti) ? "#ffce00;" : "#e0e0e0;"));
            ratingBox.getChildren().add(star);
        }

        Label disp = new Label("Disponibilità immediata");
        disp.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 12px;");

        Label prezzoLabel = new Label(String.format("€%.2f", sDB.getPrezzo()));
        prezzoLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        // Selettori
        HBox selectorsBox = new HBox(10);
        selectorsBox.setAlignment(Pos.CENTER_LEFT);
        String styleSelettori = "-fx-background-color: #ff0000; -fx-text-fill: white; -fx-mark-color: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 13px; -fx-padding: 3 10; -fx-cursor: hand;";

        ComboBox<String> comboTaglia = new ComboBox<>();
        comboTaglia.getItems().addAll("38", "39", "40", "41", "42", "43", "44", "45", "46");
        comboTaglia.setValue(sPreferita.getTaglia() > 0 ? String.valueOf((int)sPreferita.getTaglia()) : "40");
        comboTaglia.setStyle(styleSelettori);
        comboTaglia.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(item); setTextFill(javafx.scene.paint.Color.WHITE); }
            }
        });

        ComboBox<String> comboColore = new ComboBox<>();
        comboColore.getItems().addAll("Standard", "Black/White", "Limited Edition (+20€)", "Custom Gold (+50€)");
        comboColore.setValue("Standard");
        comboColore.setStyle(styleSelettori);
        comboColore.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(item); setTextFill(javafx.scene.paint.Color.WHITE); }
            }
        });

        Runnable aggiornaDati = () -> {
            try {
                int t = Integer.parseInt(comboTaglia.getValue());
                String c = comboColore.getValue();
                double nuovoPrezzo = gestore.calcolaPrezzoDinamico(sDB, t, c);
                prezzoLabel.setText(String.format("€%.2f", nuovoPrezzo));
                sPreferita.setTaglia(t);
                sPreferita.setPrezzo(nuovoPrezzo);
                String base = sDB.getModello().split("\\(")[0].trim();
                sPreferita.setModello(c.equals("Standard") ? base : base + " (" + c + ")");
            } catch (Exception ex) {}
        };
        comboTaglia.setOnAction(e -> aggiornaDati.run());
        comboColore.setOnAction(e -> aggiornaDati.run());

        // Animazioni selettori
        comboTaglia.setOnMouseEntered(e -> zoom(comboTaglia, 1.05));
        comboTaglia.setOnMouseExited(e -> zoom(comboTaglia, 1.0));
        comboColore.setOnMouseEntered(e -> zoom(comboColore, 1.05));
        comboColore.setOnMouseExited(e -> zoom(comboColore, 1.0));

        selectorsBox.getChildren().addAll(new Label("Taglia:"), comboTaglia, new Label("Colore:"), comboColore);
        centerBox.getChildren().addAll(nome, ratingBox, disp, prezzoLabel, selectorsBox);

        // --- 3. DESTRA: Bottoni in linea ---
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnAddCarrello = new Button("Aggiungi al carrello");
        btnAddCarrello.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand; -fx-padding: 10 18;");
        btnAddCarrello.setOnAction(e -> {
            Sessione.getInstance().aggiungiAlCarrello(sPreferita);
            new Alert(Alert.AlertType.INFORMATION, "Aggiunto al carrello!").showAndWait();
        });

        Button btnAcquista = new Button("Acquista ora");
        btnAcquista.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; -fx-cursor: hand; -fx-padding: 10 18;");
        btnAcquista.setOnAction(e -> {
            Sessione.getInstance().aggiungiAlCarrello(sPreferita);
            handleCarrello(e);
        });

        btnAddCarrello.setOnMouseEntered(e -> zoom(btnAddCarrello, 1.05));
        btnAddCarrello.setOnMouseExited(e -> zoom(btnAddCarrello, 1.0));
        btnAcquista.setOnMouseEntered(e -> zoom(btnAcquista, 1.05));
        btnAcquista.setOnMouseExited(e -> zoom(btnAcquista, 1.0));

        buttonsBox.getChildren().addAll(btnAddCarrello, btnAcquista);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(leftGroup, centerBox, spacer, buttonsBox);
        return card;
    }

    private void animazioneTitoloEOpen(Scarpa s, Node nodo) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), nodo);
        st.setByX(0.15); st.setByY(0.15);
        st.setCycleCount(2); st.setAutoReverse(true);
        st.setOnFinished(e -> apriDettaglio(s, nodo));
        st.play();
    }

    private void apriDettaglio(Scarpa s, Node sourceNode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/DettaglioProdotto.fxml"));
            Parent root = loader.load();
            DettaglioProdottoGUIController dp = loader.getController();
            dp.setDettagliScarpa(s);
            dp.setProvenienza("/com/sneakup/view/Preferiti.fxml");
            dp.setStatoPrecedente(prevBrand, prevCategoria, prevGenere, prevRicerca);
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handlePreferiti(ActionEvent event) { caricaPreferiti(); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Stato ordine in arrivo").showAndWait(); }

    @FXML private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/Preferiti.fxml", prevBrand, prevGenere, prevCategoria, prevRicerca, scarpaPrecedente);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Parent p = barraAnimata.getParent(); double x = p.sceneToLocal(b.getMinX(), 0).getX(); barraAnimata.setLayoutX(x); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
}