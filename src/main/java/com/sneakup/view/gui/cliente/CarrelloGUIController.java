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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

// Import dei controller per il casting nel tasto indietro
import com.sneakup.view.gui.cliente.ListaProdottiGUIController;
import com.sneakup.view.gui.cliente.SelezioneCategoriaGUIController;
import com.sneakup.view.gui.cliente.VisualizzaCatalogoGUIController;
import com.sneakup.view.gui.cliente.DettaglioProdottoGUIController;

public class CarrelloGUIController {

    @FXML private VBox containerProdotti;
    @FXML private Label lblSubtotale, lblIva, lblTotale;
    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;

    // --- VARIABILI MEMORIA (Stato della navigazione) ---
    private String paginaPrecedente = "/com/sneakup/view/Benvenuto.fxml";
    private String prevBrand;
    private String prevGenere;
    private String prevCategoria;
    private String prevRicerca;
    private Scarpa scarpaProvenienza; // AGGIUNTO: Memorizza la scarpa se veniamo dal Dettaglio

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
        aggiornaCarrello();
    }

    /**
     * FIRMA AGGIORNATA: Ora accetta anche l'oggetto Scarpa per il ritorno al Dettaglio
     */
    public void setProvenienza(String fxml, String brand, String genere, String categoria, String ricerca, Scarpa scarpa) {
        this.paginaPrecedente = fxml;
        this.prevBrand = brand;
        this.prevGenere = genere;
        this.prevCategoria = categoria;
        this.prevRicerca = ricerca;
        this.scarpaProvenienza = scarpa; // Salviamo la scarpa per il ritorno
    }

    @FXML
    private void handleIndietro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(paginaPrecedente));
            Parent root = loader.load();
            Object controller = loader.getController();

            // CASO 1: Ritorno alla Lista Prodotti (Ripristino filtri)
            if (controller instanceof ListaProdottiGUIController) {
                ListaProdottiGUIController lpCtrl = (ListaProdottiGUIController) controller;
                if (prevRicerca != null && !prevRicerca.isEmpty()) {
                    lpCtrl.setRicercaGlobale(prevRicerca);
                } else {
                    lpCtrl.setFiltri(prevBrand, prevCategoria, prevGenere);
                }
            }
            // CASO 2: Ritorno al Dettaglio Prodotto (Passaggio dati scarpa)
            else if (controller instanceof DettaglioProdottoGUIController && scarpaProvenienza != null) {
                DettaglioProdottoGUIController dpCtrl = (DettaglioProdottoGUIController) controller;
                dpCtrl.setDettagliScarpa(scarpaProvenienza);
                dpCtrl.setStatoPrecedente(prevBrand, prevCategoria, prevGenere, prevRicerca);
            }
            // CASO 3: Ritorno alla selezione categoria
            else if (controller instanceof SelezioneCategoriaGUIController) {
                ((SelezioneCategoriaGUIController) controller).setDati(prevGenere, prevBrand);
            }
            // CASO 4: Ritorno al catalogo brand
            else if (controller instanceof VisualizzaCatalogoGUIController) {
                ((VisualizzaCatalogoGUIController) controller).setBrand(prevBrand);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            naviga("/com/sneakup/view/Benvenuto.fxml", event);
        }
    }

    // --- LOGICA CORE CARRELLO ---

    private void aggiornaCarrello() {
        if (containerProdotti == null) return;

        containerProdotti.getChildren().clear();
        List<Scarpa> carrello = Sessione.getInstance().getCarrello();
        double subTotale = 0.0;

        if (carrello.isEmpty()) {
            Label empty = new Label("Il tuo carrello è vuoto.");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: grey; -fx-padding: 40 0 0 0;");
            containerProdotti.setAlignment(Pos.TOP_CENTER);
            containerProdotti.getChildren().add(empty);
        } else {
            containerProdotti.setAlignment(Pos.TOP_LEFT);
            for (Scarpa s : carrello) {
                subTotale += s.getPrezzo();
                containerProdotti.getChildren().add(creaRigaProdotto(s));
            }
        }

        double iva = subTotale * 0.22;
        if (lblSubtotale != null) lblSubtotale.setText(String.format("%.2f€", subTotale));
        if (lblIva != null) lblIva.setText(String.format("%.2f€", iva));
        if (lblTotale != null) lblTotale.setText(String.format("%.2f€", subTotale + iva));
    }

    private HBox creaRigaProdotto(Scarpa s) {
        HBox riga = new HBox(20);
        riga.setPadding(new Insets(15));
        riga.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        riga.setAlignment(Pos.CENTER_LEFT);

        ImageView img = new ImageView();
        img.setFitHeight(100); img.setFitWidth(120); img.setPreserveRatio(true);
        try {
            String path = (s.getUrlImmagine() != null) ? s.getUrlImmagine() : "/images/scarpa 1.png";
            if (getClass().getResource(path) != null) {
                img.setImage(new Image(getClass().getResource(path).toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine: " + s.getUrlImmagine());
        }

        VBox info = new VBox(5);
        Label nome = new Label(s.getModello().toUpperCase());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label taglia = new Label("Taglia: EU " + (int)s.getTaglia());
        taglia.setStyle("-fx-text-fill: grey;");
        info.getChildren().addAll(nome, taglia);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox prezzi = new VBox(10);
        prezzi.setAlignment(Pos.CENTER_RIGHT);
        Label prezzo = new Label(String.format("%.2f€", s.getPrezzo()));
        prezzo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Button btnRimuovi = new Button("Rimuovi");
        btnRimuovi.setStyle("-fx-background-color: #fce4e4; -fx-text-fill: #c0392b; -fx-cursor: hand; -fx-background-radius: 5;");
        btnRimuovi.setOnAction(e -> {
            Sessione.getInstance().rimuoviDalCarrello(s);
            aggiornaCarrello();
        });

        prezzi.getChildren().addAll(prezzo, btnRimuovi);
        riga.getChildren().addAll(img, info, spacer, prezzi);
        return riga;
    }

    @FXML
    private void handleCheckout(ActionEvent event) {
        if (Sessione.getInstance().getCarrello().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Il carrello è vuoto!").showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Procedendo al pagamento sicuro...").showAndWait();
    }

    // --- NAVIGAZIONE E HEADER ---

    @FXML private void handleReloadHome(ActionEvent event) { naviga("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { naviga("/com/sneakup/view/Benvenuto.fxml", event); }

    @FXML private void handlePreferiti(ActionEvent event) {
        if(Sessione.getInstance().isLoggato()) naviga("/com/sneakup/view/Preferiti.fxml", event);
        else new Alert(Alert.AlertType.WARNING, "Accedi per vedere i preferiti").showAndWait();
    }

    @FXML private void handleLoginGenerico(ActionEvent event) { naviga("/com/sneakup/view/Login.fxml", event); }

    @FXML private void handleVaiAreaPersonale(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/AreaPersonale.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch(IOException ex) { ex.printStackTrace(); }
    }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Point2D loc = barraAnimata.getParent().sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    @FXML public void sottolineaUser(MouseEvent event) { if (lblUser != null) lblUser.setUnderline(true); }
    @FXML public void ripristinaUser(MouseEvent event) { if (lblUser != null) lblUser.setUnderline(false); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    private void naviga(String fxml, java.util.EventObject e) {
        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML
    private void handleCarrello(ActionEvent event) {
        aggiornaCarrello();
    }

    @FXML
    private void handleStatoOrdine(ActionEvent event) {
        new Alert(Alert.AlertType.INFORMATION, "Servizio di tracking in manutenzione.").showAndWait();
    }
}