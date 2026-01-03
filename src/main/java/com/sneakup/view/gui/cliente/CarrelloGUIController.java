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
import javafx.geometry.Point2D;
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

public class CarrelloGUIController {

    @FXML private VBox boxProdotti;
    @FXML private Label lblTotale, lblTotaleFinale, lblSpedizione, lblUser;
    @FXML private Button btnLogin;
    @FXML private Region barraAnimata;
    @FXML private ScrollPane scrollPane;

    // Variabili per il tasto "Indietro"
    private String prevFxml, prevBrand, prevGen, prevCat, prevRicerca;
    private Scarpa prevScarpa;

    private final GestoreProdotti gestore = new GestoreProdotti();

    public void setProvenienza(String fxml, String brand, String gen, String cat, String ricerca, Scarpa s) {
        this.prevFxml = fxml;
        this.prevBrand = brand;
        this.prevGen = gen;
        this.prevCat = cat;
        this.prevRicerca = ricerca;
        this.prevScarpa = s;
    }

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true); lblUser.setManaged(true);
            }
        }
        aggiornaCarrello();
    }

    private void aggiornaCarrello() {
        if (boxProdotti == null) return;
        boxProdotti.getChildren().clear();

        List<Scarpa> carrello = Sessione.getInstance().getCarrello();

        if (carrello.isEmpty()) {
            Label vuoto = new Label("Il tuo carrello è vuoto.");
            vuoto.setStyle("-fx-font-size: 18px; -fx-text-fill: gray; -fx-padding: 20;");
            boxProdotti.getChildren().add(vuoto);
            lblSpedizione.setText("€0.00");
            lblTotale.setText("€0.00");
            lblTotaleFinale.setText("€0.00");
        } else {
            for (Scarpa s : carrello) {
                boxProdotti.getChildren().add(creaCardCarrelloModificabile(s));
            }
            lblSpedizione.setText("Gratuita");
            ricalcolaTotali();
        }
    }

    private void ricalcolaTotali() {
        double totale = 0.0;
        for (Scarpa s : Sessione.getInstance().getCarrello()) {
            totale += s.getPrezzo();
        }
        if (lblTotale != null) lblTotale.setText(String.format("€%.2f", totale));
        if (lblTotaleFinale != null) lblTotaleFinale.setText(String.format("€%.2f", totale));
    }

    // --- CARD MODIFICABILE AGGIORNATA ---
    private HBox creaCardCarrelloModificabile(Scarpa s) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #eeeeee; -fx-border-radius: 8; -fx-background-radius: 8;");

        // 1. Immagine
        ImageView img = new ImageView();
        try {
            String path = (s.getUrlImmagine() != null) ? s.getUrlImmagine() : "/images/scarpa 1.png";
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) {}

        img.setFitHeight(150);
        img.setFitWidth(150);
        img.setPreserveRatio(true);

        // 2. Info e Controlli
        VBox info = new VBox(10);
        info.setAlignment(Pos.CENTER_LEFT);

        String nomePulito = s.getModello().split("\\(")[0].trim();
        Label nome = new Label(nomePulito);
        nome.setFont(Font.font("System", FontWeight.BOLD, 20));

        String styleSelettori = "-fx-background-color: #ff0000; -fx-text-fill: white; -fx-mark-color: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14px; -fx-padding: 5 10;";

        // Taglia
        HBox rigaTaglia = new HBox(10);
        rigaTaglia.setAlignment(Pos.CENTER_LEFT);
        Label lblTaglia = new Label("Taglia:");
        lblTaglia.setStyle("-fx-font-size: 14px;");

        ComboBox<String> comboTaglia = new ComboBox<>();
        comboTaglia.getItems().addAll("38", "39", "40", "41", "42", "43", "44", "45", "46");
        comboTaglia.setValue(String.valueOf((int)s.getTaglia()));
        comboTaglia.setStyle(styleSelettori + "-fx-pref-width: 90px;");
        comboTaglia.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(item); setTextFill(javafx.scene.paint.Color.WHITE); }
            }
        });
        rigaTaglia.getChildren().addAll(lblTaglia, comboTaglia);

        // Colore
        HBox rigaColore = new HBox(10);
        rigaColore.setAlignment(Pos.CENTER_LEFT);
        Label lblColore = new Label("Colore:");
        lblColore.setStyle("-fx-font-size: 14px;");

        ComboBox<String> comboColore = new ComboBox<>();
        comboColore.getItems().addAll("Standard", "Black/White", "Limited Edition (+20€)", "Custom Gold (+50€)");
        String coloreAttuale = "Standard";
        if (s.getModello().contains("Limited")) coloreAttuale = "Limited Edition (+20€)";
        else if (s.getModello().contains("Gold")) coloreAttuale = "Custom Gold (+50€)";
        else if (s.getModello().contains("Black")) coloreAttuale = "Black/White";
        comboColore.setValue(coloreAttuale);
        comboColore.setStyle(styleSelettori + "-fx-pref-width: 200px;");
        comboColore.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else { setText(item); setTextFill(javafx.scene.paint.Color.WHITE); }
            }
        });
        rigaColore.getChildren().addAll(lblColore, comboColore);

        // Stelle
        double mediaVoti = gestore.getMediaVoti(s.getId());
        HBox stelleBox = new HBox(1);
        for(int i=1; i<=5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (i <= Math.round(mediaVoti) ? "#ffce00;" : "#e0e0e0;"));
            stelleBox.getChildren().add(star);
        }

        info.getChildren().addAll(nome, stelleBox, rigaTaglia, rigaColore);
        HBox.setHgrow(info, Priority.ALWAYS);

        // 3. Prezzo e Rimuovi
        VBox prezziBox = new VBox(15);
        prezziBox.setAlignment(Pos.CENTER_RIGHT);

        Label lblPrezzoSingolo = new Label(String.format("€%.2f", s.getPrezzo()));
        lblPrezzoSingolo.setFont(Font.font("System", FontWeight.BOLD, 26));
        lblPrezzoSingolo.setStyle("-fx-text-fill: #333333;");

        Button btnRimuovi = new Button("RIMUOVI");
        btnRimuovi.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #d32f2f; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 10 20;");
        btnRimuovi.setOnAction(e -> {
            Sessione.getInstance().rimuoviDalCarrello(s);
            aggiornaCarrello();
        });
        btnRimuovi.setOnMouseEntered(e -> zoom(btnRimuovi, 1.1));
        btnRimuovi.setOnMouseExited(e -> zoom(btnRimuovi, 1.0));

        prezziBox.getChildren().addAll(lblPrezzoSingolo, btnRimuovi);

        // Logica aggiornamento dinamico
        Scarpa scarpaDB = gestore.recuperaScarpaPerId(s.getId());
        Runnable aggiornaPrezzo = () -> {
            int t = (int) s.getTaglia();
            try { t = Integer.parseInt(comboTaglia.getValue()); s.setTaglia(t); } catch (Exception ex) {}
            String c = comboColore.getValue();
            double nuovoPrezzo = gestore.calcolaPrezzoDinamico(scarpaDB, t, c);
            if (c != null && !c.equals("Standard")) s.setModello(nomePulito + " (" + c + ")");
            else s.setModello(nomePulito);
            s.setPrezzo(nuovoPrezzo);
            lblPrezzoSingolo.setText(String.format("€%.2f", s.getPrezzo()));
            ricalcolaTotali();
        };

        comboTaglia.setOnAction(e -> aggiornaPrezzo.run());
        comboColore.setOnAction(e -> aggiornaPrezzo.run());

        card.getChildren().addAll(img, info, prezziBox);
        return card;
    }

    // --- NAVIGAZIONE ---

    @FXML private void handleProcediCheckout(ActionEvent event) {
        if (Sessione.getInstance().getCarrello().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Il carrello è vuoto!").showAndWait();
            return;
        }
        if (!Sessione.getInstance().isLoggato()) {
            new Alert(Alert.AlertType.WARNING, "Devi effettuare il login per completare l'acquisto.").showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Ordine inviato con successo! Grazie per l'acquisto.").showAndWait();
        Sessione.getInstance().svuotaCarrello();
        aggiornaCarrello();
    }

    @FXML private void handleIndietro(ActionEvent event) {
        if (prevFxml == null) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); return; }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(prevFxml));
            Parent root = loader.load();
            Object controller = loader.getController();

            // Ripristina lo stato del controller precedente
            if (controller instanceof ListaProdottiGUIController) {
                ((ListaProdottiGUIController) controller).setFiltri(prevBrand, prevCat, prevGen);
            } else if (controller instanceof DettaglioProdottoGUIController) {
                ((DettaglioProdottoGUIController) controller).setDettagliScarpa(prevScarpa);
                ((DettaglioProdottoGUIController) controller).setStatoPrecedente(prevBrand, prevCat, prevGen, prevRicerca);
            } else if (controller instanceof SelezioneCategoriaGUIController) {
                ((SelezioneCategoriaGUIController) controller).setDati(prevGen, prevBrand);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
        }
    }

    // CORREZIONE QUI: Metodo handleVaiAlCarrello corretto
    @FXML private void handleVaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController ctrl = loader.getController();
            // Ricarichiamo mantenendo la stessa provenienza di prima
            ctrl.setProvenienza(prevFxml, prevBrand, prevGen, prevCat, prevRicerca, prevScarpa);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Alias nel caso l'FXML usi "handleCarrello" invece di "handleVaiAlCarrello"
    @FXML private void handleCarrello(ActionEvent event) {
        handleVaiAlCarrello(event);
    }

    @FXML private void handleLoginGenerico(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Parent root = loader.load();
            com.sneakup.view.gui.common.LoginGUIController loginCtrl = loader.getController();
            loginCtrl.setProvenienza("/com/sneakup/view/Carrello.fxml", null, null, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Funzione non disponibile.").showAndWait(); }
    @FXML private void handlePreferiti(ActionEvent event) {
        if(!Sessione.getInstance().isLoggato()) new Alert(Alert.AlertType.WARNING, "Accedi prima!").showAndWait();
        else navigaVerso("/com/sneakup/view/Preferiti.fxml", event);
    }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Parent p = barraAnimata.getParent(); Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY()); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
}