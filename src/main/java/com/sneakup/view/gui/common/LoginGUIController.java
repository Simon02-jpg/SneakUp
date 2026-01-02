package com.sneakup.view.gui.common;

import com.sneakup.controller.LoginController;
import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import com.sneakup.util.AlertUtils;
import com.sneakup.exception.SneakUpException;
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

    // NUOVO METODO: Accetta tutti i dettagli necessari per tornare indietro
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

        try {
            UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();
            Utente u = utenteDAO.recuperaDatiUtente(inputEmail);

            if (u != null && u.getPassword().equals(pwd)) {
                String ruolo = u.getUsername().equalsIgnoreCase("seller") ? "ADMIN" : "CLIENTE";
                Sessione.getInstance().login(u.getUsername(), ruolo);

                System.out.println("Login effettuato: " + u.getUsername());

                if (ruolo.equals("ADMIN")) {
                    navigaVerso(event, "/com/sneakup/view/AreaVenditore.fxml");
                } else {
                    // --- LOGICA DI RITORNO ALLA PAGINA PRECEDENTE ---
                    if (paginaPrecedente != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(paginaPrecedente));
                            Parent root = loader.load();

                            // Controlliamo in quale pagina dobbiamo tornare e reimpostiamo i dati
                            Object controller = loader.getController();

                            // CASO 1: Torno al Catalogo (Home Brand)
                            if (controller instanceof VisualizzaCatalogoGUIController) {
                                ((VisualizzaCatalogoGUIController) controller).setBrand(brandPrecedente);
                            }
                            // CASO 2: Torno alla Selezione Categorie (Corsa/Basket/Calcio)
                            else if (controller instanceof SelezioneCategoriaGUIController) {
                                // Ripristino Brand e Genere (es. NIKE - UOMO)
                                String gen = (generePrecedente != null) ? generePrecedente : "CATEGORIE";
                                ((SelezioneCategoriaGUIController) controller).setDati(gen, brandPrecedente);
                            }
                            // CASO 3: Torno alla Lista Prodotti (Le scarpe)
                            else if (controller instanceof ListaProdottiGUIController) {
                                // Ripristino Brand, Categoria e Genere (es. NIKE - CORSA - UOMO)
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
                        // Se non c'è una pagina precedente, vai alla Home
                        navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml");
                    }
                }

            } else {
                AlertUtils.mostraErrore("Email o password errati.");
            }

        } catch (SneakUpException e) {
            AlertUtils.mostraErrore("Errore Database: " + e.getMessage());
        }
    }

    // --- ALTRI METODI DI NAVIGAZIONE ---
    @FXML private void handlePasswordDimenticata(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/RecuperoPassword.fxml"); }
    @FXML private void handleRegistrazione(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/Registrazione.fxml"); }
    @FXML private void handleReloadHome(ActionEvent event) { navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml"); }
    @FXML private void handleReloadHome(MouseEvent event) { navigaVerso(event, "/com/sneakup/view/Benvenuto.fxml"); }
    @FXML private void handleLoginGoogle(ActionEvent event) { AlertUtils.mostraInfo("Non disponibile."); }
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            // Passiamo la provenienza di default (Benvenuto)
            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/Login.fxml", null, null, null, null,null);

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