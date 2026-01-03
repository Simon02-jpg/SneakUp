package com.sneakup.view.gui.cliente;

import com.sneakup.controller.GestoreProdotti;
import com.sneakup.model.Sessione;
import com.sneakup.model.domain.Recensione;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DettaglioProdottoGUIController {

    @FXML private ImageView imgScarpa;
    @FXML private Label lblModello, lblPrezzo, lblInfoExtra;
    @FXML private Text txtDescrizione;
    @FXML private ComboBox<String> comboTaglia, comboColore;
    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;
    @FXML private HBox containerStelle;
    @FXML private Label lblNumeroVoti;
    @FXML private VBox containerRecensioni;

    // Riferimenti ai bottoni per applicare le animazioni via codice se non settati da FXML
    @FXML private Button btnAcquista, btnAggiungiCarrello, btnIndietro;

    private Scarpa scarpaBase;
    private double prezzoFinaleCalcolato;

    private String prevBrand, prevCategoria, prevGenere, prevRicerca;
    private String fxmlProvenienza = "/com/sneakup/view/ListaProdotti.fxml";

    private final GestoreProdotti gestore = new GestoreProdotti();

    public void setProvenienza(String fxmlPath) {
        this.fxmlProvenienza = fxmlPath;
    }

    public void setStatoPrecedente(String brand, String cat, String gen, String ricerca) {
        this.prevBrand = brand;
        this.prevCategoria = cat;
        this.prevGenere = gen;
        this.prevRicerca = ricerca;
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

        // --- STILE E LOGICA COMBOBOX ---
        String styleSelettori = "-fx-background-color: #ff0000; -fx-text-fill: white; -fx-mark-color: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14px; -fx-padding: 5 10;";
        if (comboTaglia != null) {
            if (comboTaglia.getItems().isEmpty()) comboTaglia.getItems().addAll("38", "39", "40", "41", "42", "43", "44", "45", "46");
            comboTaglia.setStyle(styleSelettori);
            comboTaglia.setButtonCell(new ListCell<>() { @Override protected void updateItem(String item, boolean empty) { super.updateItem(item, empty); if (!empty && item!=null) { setText(item); setTextFill(javafx.scene.paint.Color.WHITE); } else setText(null); }});
            comboTaglia.setOnAction(e -> aggiornaPrezzoDinamico());
        }
        if (comboColore != null) {
            if (comboColore.getItems().isEmpty()) comboColore.getItems().addAll("Standard", "Black/White", "Limited Edition (+20€)", "Custom Gold (+50€)");
            comboColore.setStyle(styleSelettori);
            comboColore.setButtonCell(new ListCell<>() { @Override protected void updateItem(String item, boolean empty) { super.updateItem(item, empty); if (!empty && item!=null) { setText(item); setTextFill(javafx.scene.paint.Color.WHITE); } else setText(null); }});
            comboColore.setOnAction(e -> aggiornaPrezzoDinamico());
        }

        // --- APPLICAZIONE ANIMAZIONI AI BOTTONI ---
        // Se hai dato dei fx:id ai bottoni nel file FXML, questo li rende interattivi automaticamente
        configuraAnimazioneBottone(btnAcquista);
        configuraAnimazioneBottone(btnAggiungiCarrello);
        configuraAnimazioneBottone(btnIndietro);
    }

    private void configuraAnimazioneBottone(Button btn) {
        if (btn != null) {
            btn.setOnMouseEntered(e -> zoom(btn, 1.05));
            btn.setOnMouseExited(e -> zoom(btn, 1.0));
        }
    }

    public void setDettagliScarpa(Scarpa s) {
        this.scarpaBase = s;
        this.prezzoFinaleCalcolato = s.getPrezzo();
        if (lblModello != null) lblModello.setText(s.getModello());
        if (lblPrezzo != null) lblPrezzo.setText(String.format("€%.2f", s.getPrezzo()));
        if (txtDescrizione != null) txtDescrizione.setText((s.getDescrizione() != null && !s.getDescrizione().isEmpty()) ? s.getDescrizione() : "Descrizione tecnica non disponibile.");

        if (imgScarpa != null && s.getUrlImmagine() != null) {
            try {
                imgScarpa.setImage(new Image(getClass().getResource(s.getUrlImmagine()).toExternalForm()));
            } catch (Exception e) {
                System.err.println("Errore caricamento immagine: " + s.getUrlImmagine());
            }
        }

        if (comboTaglia != null) {
            String tagliaDb = String.valueOf((int)s.getTaglia());
            if (comboTaglia.getItems().contains(tagliaDb)) comboTaglia.setValue(tagliaDb);
            else comboTaglia.getSelectionModel().select(0);
        }
        if (comboColore != null) comboColore.getSelectionModel().select(0);

        aggiornaGraficaStelle();
        caricaRecensioni();
        aggiornaPrezzoDinamico();
    }

    private void aggiornaPrezzoDinamico() {
        if (scarpaBase == null) return;
        int taglia = 38;
        try { if (comboTaglia.getValue() != null) taglia = Integer.parseInt(comboTaglia.getValue()); } catch (Exception e) {}
        String colore = comboColore.getValue();
        this.prezzoFinaleCalcolato = gestore.calcolaPrezzoDinamico(scarpaBase, taglia, colore);
        if (lblPrezzo != null) lblPrezzo.setText(String.format("€%.2f", this.prezzoFinaleCalcolato));

        double sovrapprezzo = this.prezzoFinaleCalcolato - scarpaBase.getPrezzo();
        if (lblInfoExtra != null) {
            if (sovrapprezzo > 0) lblInfoExtra.setText(String.format("(Sovrapprezzo variante: +€%.2f)", sovrapprezzo));
            else lblInfoExtra.setText("");
        }
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlProvenienza));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof ListaProdottiGUIController) {
                ListaProdottiGUIController ctrl = (ListaProdottiGUIController) controller;
                if (prevRicerca != null && !prevRicerca.isEmpty()) ctrl.setRicercaGlobale(prevRicerca);
                else ctrl.setFiltri(prevBrand, prevCategoria, prevGenere);
            }
            else if (controller instanceof PreferitiGUIController) {
                ((PreferitiGUIController) controller).setProvenienza(prevBrand, prevCategoria, prevGenere, prevRicerca, null, null);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAggiungiAlCarrello(ActionEvent event) {
        if (scarpaBase != null) {
            Scarpa scarpaDaComprare = new Scarpa();
            scarpaDaComprare.setId(scarpaBase.getId());
            scarpaDaComprare.setMarca(scarpaBase.getMarca());
            scarpaDaComprare.setCategoria(scarpaBase.getCategoria());
            scarpaDaComprare.setGenere(scarpaBase.getGenere());
            scarpaDaComprare.setUrlImmagine(scarpaBase.getUrlImmagine());
            scarpaDaComprare.setDescrizione(scarpaBase.getDescrizione());
            scarpaDaComprare.setPrezzo(this.prezzoFinaleCalcolato);

            try {
                scarpaDaComprare.setTaglia(Double.parseDouble(comboTaglia.getValue()));
            } catch (Exception e) {
                scarpaDaComprare.setTaglia(scarpaBase.getTaglia());
            }

            String coloreScelto = comboColore.getValue();
            if (coloreScelto != null && !coloreScelto.equals("Standard"))
                scarpaDaComprare.setModello(scarpaBase.getModello() + " (" + coloreScelto + ")");
            else
                scarpaDaComprare.setModello(scarpaBase.getModello());

            Sessione.getInstance().aggiungiAlCarrello(scarpaDaComprare);
            new Alert(Alert.AlertType.INFORMATION, "Aggiunto al carrello!").showAndWait();
        }
    }

    @FXML
    private void handleAcquista(ActionEvent event) {
        handleAggiungiAlCarrello(event);
        handleVaiAlCarrello(event);
    }

    private void caricaRecensioni() {
        if (containerRecensioni == null) return;
        containerRecensioni.getChildren().clear();
        List<Recensione> recensioni = gestore.getRecensioni(scarpaBase.getId());
        if (recensioni.isEmpty()) {
            Label noRec = new Label("Nessuna recensione per questo prodotto.");
            noRec.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 10;");
            containerRecensioni.getChildren().add(noRec);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Recensione r : recensioni) {
            VBox box = new VBox(5);
            box.setStyle("-fx-border-color: #eeeeee; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: #fafafa;");
            HBox header = new HBox(10);
            Label userLbl = new Label(r.getUsername()); userLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Label dataLbl = new Label((r.getDataInserimento() != null) ? sdf.format(r.getDataInserimento()) : ""); dataLbl.setStyle("-fx-text-fill: #999999; -fx-font-size: 12px;");
            header.getChildren().addAll(userLbl, dataLbl);
            HBox stelleBox = new HBox(2);
            for (int i = 1; i <= 5; i++) { Label stella = new Label("★"); stella.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (i <= r.getVoto() ? "#ffce00;" : "#cccccc;")); stelleBox.getChildren().add(stella); }
            Label testoLbl = new Label(r.getTesto()); testoLbl.setWrapText(true); testoLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
            box.getChildren().addAll(header, stelleBox, testoLbl);
            containerRecensioni.getChildren().add(box);
        }
    }

    private void aggiornaGraficaStelle() {
        if (containerStelle == null) return;
        double media = gestore.getMediaVoti(scarpaBase.getId());
        containerStelle.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            Label stella = new Label("★");
            stella.setStyle("-fx-font-size: 24px; -fx-text-fill: " + (i <= Math.round(media) ? "#ffce00;" : "#cccccc;"));
            containerStelle.getChildren().add(stella);
        }
        if (lblNumeroVoti != null) lblNumeroVoti.setText(String.format("(%.1f/5)", media));
    }

    @FXML
    private void handleVaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/DettaglioProdotto.fxml", prevBrand, prevGenere, prevCategoria, prevRicerca, scarpaBase);
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
                ctrl.setProvenienza("/com/sneakup/view/DettaglioProdotto.fxml", prevBrand, prevGenere, prevCategoria, prevRicerca, scarpaBase);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            new Alert(Alert.AlertType.WARNING, "Accedi per salvare questa scarpa nei preferiti!").showAndWait();
        }
    }

    // --- UTILS ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Point2D loc = barraAnimata.getParent().sceneToLocal(b.getMinX(), 0); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(false); }

    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        if (n == null) return;
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s);
        st.setToY(s);
        st.play();
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
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Servizio non disponibile.").showAndWait(); }
}