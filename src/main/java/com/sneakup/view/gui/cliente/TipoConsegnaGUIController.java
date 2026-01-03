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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class TipoConsegnaGUIController {

    @FXML private Button btnLogin, btnRitiro, btnSpedizione, btnIndietro;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;

    // Dati navigazione
    private String prevFxml;
    private Scarpa scarpaDettaglio;
    private Scarpa prodottoAcquistoSingolo;
    private String prevBrand, prevGen, prevCat, prevRicerca;

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
    }

    public void setDatiNavigazione(String prevFxml, Scarpa scarpaDettaglio, Scarpa prodottoAcquisto,
                                   String brand, String gen, String cat, String ricerca) {
        this.prevFxml = prevFxml;
        this.scarpaDettaglio = scarpaDettaglio;
        this.prodottoAcquistoSingolo = prodottoAcquisto;
        this.prevBrand = brand;
        this.prevGen = gen;
        this.prevCat = cat;
        this.prevRicerca = ricerca;
    }

    @FXML
    private void handleTornaIndietro(ActionEvent event) {
        try {
            String target;

            // --- FIX LOGICA INDIETRO PER EVITARE IL BLOCCO ---
            if (Sessione.getInstance().isLoggato()) {
                // SE LOGGATO: Non possiamo tornare a Pagamento.fxml (perché ci rimanderebbe qui in loop).
                // Dobbiamo tornare al Carrello o al Dettaglio Prodotto.

                if (prodottoAcquistoSingolo != null) {
                    // Caso "Acquista Ora": Torniamo al Dettaglio Prodotto
                    target = "/com/sneakup/view/DettaglioProdotto.fxml";
                } else {
                    // Caso "Carrello": Torniamo al Carrello
                    target = "/com/sneakup/view/Carrello.fxml";
                }
            } else {
                // SE OSPITE: Torniamo a Pagamento.fxml (per permettere di fare login o cambiare scelta)
                target = (prevFxml != null && !prevFxml.isEmpty()) ? prevFxml : "/com/sneakup/view/Carrello.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(target));
            Parent root = loader.load();
            Object controller = loader.getController();

            // Configurazione del controller di destinazione (passaggio dati indietro)
            if (controller instanceof PagamentoGUIController) {
                ((PagamentoGUIController) controller).setDatiNavigazione(
                        "/com/sneakup/view/Carrello.fxml",
                        scarpaDettaglio, prodottoAcquistoSingolo,
                        prevBrand, prevGen, prevCat, prevRicerca);
            }
            else if (controller instanceof CarrelloGUIController) {
                // Tornando al carrello, resettiamo la provenienza per il tasto indietro del carrello
                ((CarrelloGUIController) controller).setProvenienza(
                        "/com/sneakup/view/ListaProdotti.fxml",
                        prevBrand, prevGen, prevCat, prevRicerca, scarpaDettaglio, null);
            }
            else if (controller instanceof DettaglioProdottoGUIController) {
                DettaglioProdottoGUIController dp = (DettaglioProdottoGUIController) controller;
                // Ripristiniamo la scarpa corretta
                dp.setDettagliScarpa(scarpaDettaglio != null ? scarpaDettaglio : prodottoAcquistoSingolo);
                // Se torniamo al dettaglio, il suo "indietro" deve portare alla lista
                dp.setProvenienza("/com/sneakup/view/ListaProdotti.fxml");
                dp.setStatoPrecedente(prevBrand, prevGen, prevCat, prevRicerca);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback di sicurezza in caso di errore grave: Torna alla Home
            navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
        }
    }

    @FXML
    private void handleRitiro(ActionEvent event) {
        finalizzaOrdine("Ritiro in negozio", event);
    }

    @FXML
    private void handleSpedizione(ActionEvent event) {
        finalizzaOrdine("Spedizione a domicilio", event);
    }

    private void finalizzaOrdine(String tipo, ActionEvent event) {
        double totale = 0.0;
        if (prodottoAcquistoSingolo != null) {
            totale = prodottoAcquistoSingolo.getPrezzo();
        } else {
            // Calcolo totale carrello
            for(Scarpa s : Sessione.getInstance().getCarrello()) {
                totale += s.getPrezzo();
            }
        }

        // Calcolo Spedizione Finale
        double costoSped = 9.99; // Default

        if (tipo.contains("Ritiro")) {
            costoSped = 0.0; // Ritiro sempre gratis
        } else {
            // Spedizione domicilio: Gratis se Loggato E Totale > 100
            if (Sessione.getInstance().isLoggato() && totale > 100.00) {
                costoSped = 0.0;
            }
        }

        double totaleFinale = totale + costoSped;

        String msg = "Ordine Confermato!\nModalità: " + tipo + "\n";
        msg += String.format("Totale Articoli: €%.2f\n", totale);
        msg += (costoSped > 0) ? String.format("Spedizione: €%.2f\n", costoSped) : "Spedizione: Gratis\n";
        msg += String.format("TOTALE PAGATO: €%.2f", totaleFinale);

        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();

        // Svuota carrello se era un acquisto carrello
        if (prodottoAcquistoSingolo == null) {
            Sessione.getInstance().svuotaCarrello();
        }

        // Torna alla home
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/Benvenuto.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- NAVIGAZIONE STANDARD ---
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handlePreferiti(ActionEvent event) { navigaVerso("/com/sneakup/view/Preferiti.fxml", event); }
    @FXML private void handleStatoOrdine(ActionEvent event) { new Alert(Alert.AlertType.INFORMATION, "Funzione non disponibile.").showAndWait(); }
    @FXML private void handleTornaIndietroMenu(ActionEvent event) { handleTornaIndietro(event); }

    private void navigaVerso(String fxml, java.util.EventObject e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) (e != null ? e.getSource() : btnRitiro)).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML public void mostraEmuoviBarra(MouseEvent event) { Node source = (Node) event.getSource(); Bounds b = source.localToScene(source.getBoundsInLocal()); Point2D loc = barraAnimata.getParent().sceneToLocal(b.getMinX(), 0); barraAnimata.setLayoutX(loc.getX()); barraAnimata.setPrefWidth(b.getWidth()); barraAnimata.setOpacity(1.0); }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.05); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }
}