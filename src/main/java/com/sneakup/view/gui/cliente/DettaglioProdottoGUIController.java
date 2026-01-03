package com.sneakup.view.gui.cliente;

import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.ScarpaDAOJDBC;
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

    private Scarpa scarpaBase; // La scarpa originale dal DB
    private double prezzoFinaleCalcolato; // Il prezzo che cambia dinamicamente

    private String prevBrand, prevCategoria, prevGenere, prevRicerca;
    private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();

    public void setStatoPrecedente(String brand, String cat, String gen, String ricerca) {
        this.prevBrand = brand;
        this.prevCategoria = cat;
        this.prevGenere = gen;
        this.prevRicerca = ricerca;
    }

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);

        // Header Utente
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true); lblUser.setManaged(true);
            }
        }

        // Popolamento ComboBox
        if (comboTaglia != null && comboTaglia.getItems().isEmpty()) {
            comboTaglia.getItems().addAll("38", "39", "40", "41", "42", "43", "44", "45", "46");
        }
        if (comboColore != null && comboColore.getItems().isEmpty()) {
            comboColore.getItems().addAll("Standard", "Black/White", "Limited Edition (+20€)", "Custom Gold (+50€)");
        }

        // Listener: Appena cambi valore, ricalcola il prezzo
        if (comboTaglia != null) comboTaglia.setOnAction(e -> aggiornaPrezzoDinamico());
        if (comboColore != null) comboColore.setOnAction(e -> aggiornaPrezzoDinamico());
    }

    public void setDettagliScarpa(Scarpa s) {
        this.scarpaBase = s;
        this.prezzoFinaleCalcolato = s.getPrezzo();

        if (lblModello != null) lblModello.setText(s.getModello());
        if (lblPrezzo != null) lblPrezzo.setText(String.format("€%.2f", s.getPrezzo()));

        if (txtDescrizione != null) {
            txtDescrizione.setText((s.getDescrizione() != null && !s.getDescrizione().isEmpty())
                    ? s.getDescrizione() : "Descrizione tecnica non disponibile.");
        }

        if (imgScarpa != null && s.getUrlImmagine() != null) {
            try {
                imgScarpa.setImage(new Image(getClass().getResource(s.getUrlImmagine()).toExternalForm()));
            } catch (Exception e) {}
        }

        // Seleziona i valori di default (così l'utente vede subito cosa sta comprando)
        if (comboTaglia != null) {
            // Cerchiamo di selezionare la taglia che arriva dal DB, se presente nella lista
            String tagliaDb = String.valueOf((int)s.getTaglia());
            if (comboTaglia.getItems().contains(tagliaDb)) {
                comboTaglia.setValue(tagliaDb);
            } else {
                comboTaglia.getSelectionModel().select(0); // Altrimenti seleziona la prima
            }
        }

        if (comboColore != null) comboColore.getSelectionModel().select(0);

        aggiornaGraficaStelle();
        caricaRecensioni();
        aggiornaPrezzoDinamico();
    }

    /**
     * Logica che collega la scelta dell'utente al prezzo visualizzato
     */
    private void aggiornaPrezzoDinamico() {
        if (scarpaBase == null) return;

        double prezzo = scarpaBase.getPrezzo();
        double sovrapprezzo = 0.0;

        // 1. Controllo Taglia
        String tagliaStr = comboTaglia.getValue();
        if (tagliaStr != null) {
            try {
                int t = Integer.parseInt(tagliaStr);
                if (t >= 45) sovrapprezzo += 10.0; // Taglie grandi costano di più
            } catch (Exception e) {}
        }

        // 2. Controllo Colore
        String coloreStr = comboColore.getValue();
        if (coloreStr != null) {
            if (coloreStr.contains("Limited")) sovrapprezzo += 20.0;
            else if (coloreStr.contains("Gold")) sovrapprezzo += 50.0;
        }

        this.prezzoFinaleCalcolato = prezzo + sovrapprezzo;

        // Aggiorna la Label a video
        if (lblPrezzo != null) lblPrezzo.setText(String.format("€%.2f", this.prezzoFinaleCalcolato));

        // Aggiorna info extra
        if (lblInfoExtra != null) {
            if (sovrapprezzo > 0) lblInfoExtra.setText("(Sovrapprezzo variante: +€" + sovrapprezzo + ")");
            else lblInfoExtra.setText("");
        }
    }

    /**
     * QUI AVVIENE IL COLLEGAMENTO VERO:
     * Creiamo una NUOVA scarpa con i dati scelti nelle ComboBox
     */
    @FXML
    private void handleAggiungiAlCarrello(ActionEvent event) {
        if (scarpaBase != null) {
            // 1. CLONIAMO L'OGGETTO (Così non modifichiamo quello originale della lista)
            Scarpa scarpaDaComprare = new Scarpa();
            scarpaDaComprare.setId(scarpaBase.getId());
            scarpaDaComprare.setMarca(scarpaBase.getMarca());
            scarpaDaComprare.setCategoria(scarpaBase.getCategoria());
            scarpaDaComprare.setGenere(scarpaBase.getGenere());
            scarpaDaComprare.setUrlImmagine(scarpaBase.getUrlImmagine());
            scarpaDaComprare.setDescrizione(scarpaBase.getDescrizione());

            // 2. APPLICHIAMO LE SCELTE DELL'UTENTE (Qui avviene il collegamento!)

            // Setta il PREZZO CALCOLATO (quello che vede a schermo)
            scarpaDaComprare.setPrezzo(this.prezzoFinaleCalcolato);

            // Setta la TAGLIA SCELTA dalla tendina
            try {
                double tagliaScelta = Double.parseDouble(comboTaglia.getValue());
                scarpaDaComprare.setTaglia(tagliaScelta);
            } catch (Exception e) {
                scarpaDaComprare.setTaglia(scarpaBase.getTaglia()); // Fallback
            }

            // Setta il COLORE SCELTO (Modifichiamo il nome modello per distinguerlo nel carrello)
            String coloreScelto = comboColore.getValue();
            if (coloreScelto != null && !coloreScelto.equals("Standard")) {
                scarpaDaComprare.setModello(scarpaBase.getModello() + " (" + coloreScelto + ")");
            } else {
                scarpaDaComprare.setModello(scarpaBase.getModello());
            }

            // 3. AGGIUNGIAMO AL CARRELLO L'OGGETTO MODIFICATO
            Sessione.getInstance().aggiungiAlCarrello(scarpaDaComprare);

            new Alert(Alert.AlertType.INFORMATION,
                    "Aggiunto al carrello:\n" +
                            scarpaDaComprare.getModello() + "\n" +
                            "Taglia: " + (int)scarpaDaComprare.getTaglia() + "\n" +
                            "Prezzo: €" + String.format("%.2f", scarpaDaComprare.getPrezzo())
            ).showAndWait();
        }
    }

    @FXML
    private void handleAcquista(ActionEvent event) {
        // Simula acquisto diretto aggiungendo e andando al carrello
        handleAggiungiAlCarrello(event);
        handleVaiAlCarrello(event);
    }

    // --- GESTIONE RECENSIONI ---
    private void caricaRecensioni() {
        if (containerRecensioni == null) return;
        containerRecensioni.getChildren().clear();
        List<Recensione> recensioni = scarpaDAO.getRecensioniPerScarpa(scarpaBase.getId());

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
            Label userLbl = new Label(r.getUsername());
            userLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            String dataStr = (r.getDataInserimento() != null) ? sdf.format(r.getDataInserimento()) : "";
            Label dataLbl = new Label(dataStr);
            dataLbl.setStyle("-fx-text-fill: #999999; -fx-font-size: 12px;");
            header.getChildren().addAll(userLbl, dataLbl);

            HBox stelleBox = new HBox(2);
            for (int i = 1; i <= 5; i++) {
                Label stella = new Label("★");
                stella.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (i <= r.getVoto() ? "#ffce00;" : "#cccccc;"));
                stelleBox.getChildren().add(stella);
            }

            Label testoLbl = new Label(r.getTesto());
            testoLbl.setWrapText(true);
            testoLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

            box.getChildren().addAll(header, stelleBox, testoLbl);
            containerRecensioni.getChildren().add(box);
        }
    }

    // --- GRAFICA STELLE MEDIA ---
    private void aggiornaGraficaStelle() {
        if (containerStelle == null) return;
        double media = scarpaDAO.getMediaVoti(scarpaBase.getId());
        containerStelle.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            Label stella = new Label("★");
            stella.setStyle("-fx-font-size: 24px; -fx-text-fill: " + (i <= Math.round(media) ? "#ffce00;" : "#cccccc;"));
            containerStelle.getChildren().add(stella);
        }
        if (lblNumeroVoti != null) lblNumeroVoti.setText(String.format("(%.1f/5)", media));
    }

    // --- NAVIGAZIONE ---
    @FXML private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/ListaProdotti.fxml"));
            Parent root = loader.load();
            ListaProdottiGUIController ctrl = loader.getController();
            if (prevRicerca != null && !prevRicerca.isEmpty()) ctrl.setRicercaGlobale(prevRicerca);
            else ctrl.setFiltri(prevBrand, prevCategoria, prevGenere);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleVaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();
            // Passiamo la scarpa BASE per poter tornare indietro correttamente
            ctrl.setProvenienza("/com/sneakup/view/DettaglioProdotto.fxml", prevBrand, prevGenere, prevCategoria, prevRicerca, scarpaBase);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handlePreferiti(ActionEvent event) {
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

    // --- UTILS ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Point2D loc = barraAnimata.getParent().sceneToLocal(b.getMinX(), 0); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void sottolineaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent e) { if (lblUser != null) lblUser.setUnderline(false); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
    private void navigaSemplice(String fxml, java.util.EventObject e) { try { Parent root = FXMLLoader.load(getClass().getResource(fxml)); Stage s = (Stage)((Node)e.getSource()).getScene().getWindow(); s.setScene(new Scene(root)); } catch(Exception ex) { ex.printStackTrace(); } }
    @FXML private void handleReloadHome(ActionEvent event) { navigaSemplice("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaSemplice("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaSemplice("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaSemplice("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Servizio non disponibile.").showAndWait(); }
}