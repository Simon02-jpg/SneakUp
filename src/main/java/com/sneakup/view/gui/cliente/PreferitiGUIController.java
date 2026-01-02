package com.sneakup.view.gui.cliente;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @FXML private GridPane gridPreferiti;
    @FXML private Button btnLogin, btnHome, btnCarrello, btnStato, btnPreferiti;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;

    // --- STATO PER IL RITORNO ---
    private String fxmlPrecedente = "/com/sneakup/view/Benvenuto.fxml";
    private String prevBrand, prevGenere, prevCategoria, prevRicerca;
    private Scarpa scarpaPrecedente; // Fondamentale per tornare al Dettaglio corretta

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

    // --- METODI SET PROVENIENZA (Overloading per gestire diverse chiamate) ---

    // Per chiamate semplici (es. da Area Personale, Menu, Login)
    public void setProvenienza(String fxml) {
        this.fxmlPrecedente = fxml;
    }

    // Per chiamate complete (es. da DettaglioProdotto o ListaProdotti)
    public void setProvenienza(String fxml, String brand, String genere, String categoria, String ricerca, Scarpa scarpa) {
        this.fxmlPrecedente = fxml;
        this.prevBrand = brand;
        this.prevGenere = genere;
        this.prevCategoria = categoria;
        this.prevRicerca = ricerca;
        this.scarpaPrecedente = scarpa; // CORRETTO: Ora la variabile viene popolata
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPrecedente));
            Parent root = loader.load();
            Object controller = loader.getController();

            // Sincronizzazione degli stati al ritorno
            if (controller instanceof ListaProdottiGUIController) {
                ListaProdottiGUIController lp = (ListaProdottiGUIController) controller;
                if (prevRicerca != null) lp.setRicercaGlobale(prevRicerca);
                else lp.setFiltri(prevBrand, prevCategoria, prevGenere);
            }
            else if (controller instanceof DettaglioProdottoGUIController) {
                DettaglioProdottoGUIController dp = (DettaglioProdottoGUIController) controller;
                // Se abbiamo la scarpa salvata, la carichiamo nel dettaglio
                if (scarpaPrecedente != null) {
                    dp.setDettagliScarpa(scarpaPrecedente);
                }
                dp.setStatoPrecedente(prevBrand, prevCategoria, prevGenere, prevRicerca);
            }
            else if (controller instanceof SelezioneCategoriaGUIController) {
                ((SelezioneCategoriaGUIController) controller).setDati(prevGenere, prevBrand);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
        }
    }

    private void caricaPreferiti() {
        gridPreferiti.getChildren().clear();
        List<Scarpa> preferiti = Sessione.getInstance().getPreferiti();

        if (preferiti.isEmpty()) {
            Label empty = new Label("La tua lista dei preferiti è vuota.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-padding: 20;");
            gridPreferiti.add(empty, 0, 0);
            return;
        }

        int col = 0, row = 0;
        for (Scarpa s : preferiti) {
            gridPreferiti.add(creaCard(s), col, row);
            if (++col == 2) { col = 0; row++; }
        }
    }

    private HBox creaCard(Scarpa s) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 12; -fx-cursor: hand;");

        Label stella = new Label("★");
        stella.setStyle("-fx-font-size: 30px; -fx-text-fill: #ffce00; -fx-cursor: hand;");
        stella.setOnMouseClicked(e -> {
            e.consume(); // Evita di aprire il dettaglio cliccando sulla stella
            Sessione.getInstance().rimuoviPreferito(s);
            caricaPreferiti();
        });

        ImageView img = new ImageView();
        try {
            String path = (s.getUrlImmagine() != null) ? s.getUrlImmagine() : "/images/scarpa 1.png";
            img.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) { /* Fallback */ }
        img.setFitHeight(110); img.setFitWidth(140); img.setPreserveRatio(true);

        VBox info = new VBox(3);
        info.setAlignment(Pos.CENTER_LEFT);
        Label marca = new Label(s.getMarca().toUpperCase());
        marca.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label nome = new Label(s.getModello());
        nome.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label prezzo = new Label(String.format("€%.2f", s.getPrezzo()));
        prezzo.setFont(Font.font("System", FontWeight.BOLD, 16));

        info.getChildren().addAll(marca, nome, prezzo);
        card.getChildren().addAll(stella, img, info);
        HBox.setHgrow(info, Priority.ALWAYS);

        card.setOnMouseClicked(e -> apriDettaglio(s, e));

        return card;
    }

    private void apriDettaglio(Scarpa s, MouseEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/DettaglioProdotto.fxml"));
            Parent root = loader.load();
            DettaglioProdottoGUIController dp = loader.getController();
            dp.setDettagliScarpa(s);

            // Quando apriamo un dettaglio dai preferiti, passiamo i dati che avevamo
            dp.setStatoPrecedente(prevBrand, prevCategoria, prevGenere, prevRicerca);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- LOGICA HEADER ---
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handlePreferiti(ActionEvent event) { caricaPreferiti(); }

    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();

            // Passiamo lo stato attuale al carrello (6 parametri)
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

    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        double x = barraAnimata.getParent().sceneToLocal(b.getMinX(), 0).getX();
        barraAnimata.setLayoutX(x);
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Stato ordine in arrivo").showAndWait(); }
}