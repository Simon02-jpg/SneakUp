package com.sneakup.view.gui.common;

import com.sneakup.controller.LoginController; // USIAMO IL CONTROLLER DIRETTO
import com.sneakup.model.Sessione;
import com.sneakup.model.domain.Utente;
import com.sneakup.model.domain.Ruolo;
import com.sneakup.util.AlertUtils;
import com.sneakup.view.gui.cliente.CarrelloGUIController;
import com.sneakup.view.gui.cliente.ListaProdottiGUIController;
import com.sneakup.view.gui.cliente.SelezioneCategoriaGUIController;
import com.sneakup.view.gui.cliente.VisualizzaCatalogoGUIController;

import javafx.scene.control.Alert;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginGUIController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Region barraAnimata;

    // MODIFICA: Usiamo LoginController invece di GestoreUtenti per coerenza con il file che hai
    private final LoginController loginController = new LoginController();

    // --- DATI PER IL RITORNO ALLA PAGINA PRECEDENTE ---
    private String paginaPrecedente = null;
    private String brandPrecedente = null;
    private String generePrecedente = null;
    private String categoriaPrecedente = null;

    @FXML
    public void initialize() {
        if (barraAnimata != null) {
            barraAnimata.setOpacity(0.0);
        }
    }

    public void setProvenienza(String fxmlPath, String brand, String genere, String categoria) {
        this.paginaPrecedente = fxmlPath;
        this.brandPrecedente = brand;
        this.generePrecedente = genere;
        this.categoriaPrecedente = categoria;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String inputEmail = emailField.getText().trim();
        String pwd = passwordField.getText();

        if (inputEmail.isEmpty() || pwd.isEmpty()) {
            AlertUtils.mostraErrore("Inserisci email e password per accedere.");
            return;
        }

        // 1. Chiamata al Controller
        // Ora restituisce l'oggetto Utente completo (o null)
        Utente utenteLoggato = loginController.login(inputEmail, pwd);

        if (utenteLoggato != null) {
            // 2. Login riuscito: Aggiorna Sessione

            // Recuperiamo il ruolo vero dal Database (tramite l'oggetto Utente)
            String ruoloStringa = "CLIENTE"; // Default
            if (utenteLoggato.getRuolo() != null) {
                ruoloStringa = utenteLoggato.getRuolo().name();
            }

            // Impostiamo la sessione
            Sessione.getInstance().login(utenteLoggato.getUsername(), ruoloStringa);
            Sessione.getInstance().setUtente(utenteLoggato);

            System.out.println("Login effettuato: " + utenteLoggato.getUsername() + " | Ruolo: " + ruoloStringa);

            // Navigazione in base al Ruolo
            if (utenteLoggato.getRuolo() == Ruolo.VENDITORE) {
                // Se è un venditore, va alla sua area dedicata
                navigaVerso(event, "/com/sneakup/view/AreaVenditore.fxml");
            } else {
                // Se è un cliente, torna da dove era venuto o alla home
                gestisciRitorno(event);
            }

        } else {
            AlertUtils.mostraErrore("Email/Username o password errati.");
        }
    }

    private void gestisciRitorno(ActionEvent event) {
        if (paginaPrecedente != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(paginaPrecedente));
                Parent root = loader.load();

                Object controller = loader.getController();

                if (controller instanceof VisualizzaCatalogoGUIController) {
                    ((VisualizzaCatalogoGUIController) controller).setBrand(brandPrecedente);
                }
                else if (controller instanceof SelezioneCategoriaGUIController) {
                    String gen = (generePrecedente != null) ? generePrecedente : "CATEGORIE";
                    ((SelezioneCategoriaGUIController) controller).setDati(gen, brandPrecedente);
                }
                else if (controller instanceof ListaProdottiGUIController) {
                    ((ListaProdottiGUIController) controller).setFiltri(brandPrecedente, categoriaPrecedente, generePrecedente);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml");
            }
        } else {
            navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml");
        }
    }

    // --- ALTRI METODI DI NAVIGAZIONE ---
    @FXML private void handlePasswordDimenticata(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/RecuperoPassword.fxml"); }
    @FXML private void handleRegistrazione(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/Registrazione.fxml"); }
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml"); }
    @FXML private void handleReloadHomeMouse(MouseEvent event) { navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml"); }
    @FXML private void handleLoginGoogle(ActionEvent event) { AlertUtils.mostraInfo("Funzione Google in arrivo."); }

    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();
            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/Login.fxml", null, null, null, null, null);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore Carrello: " + e.getMessage()).showAndWait();
        }
    }

    @FXML private void handleStatoOrdine(ActionEvent event) { AlertUtils.mostraInfo("Accedi prima."); }
    @FXML private void handlePreferiti(ActionEvent event) { AlertUtils.mostraInfo("Accedi prima."); }

    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata != null) barraAnimata.setOpacity(0.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.1); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    private void zoom(Node n, double s) { ScaleTransition st = new ScaleTransition(Duration.millis(200), n); st.setToX(s); st.setToY(s); st.play(); }

    private void navigaVerso(java.util.EventObject event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore caricamento: " + fxmlPath);
        }
    }
}