package com.sneakup.view.gui.common;

import com.sneakup.controller.LoginController;
import com.sneakup.util.AlertUtils;
import com.sneakup.view.gui.cliente.CarrelloGUIController;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;

import java.io.IOException;

public class RecuperoPasswordGUIController {

    @FXML private TextField usernameField;
    @FXML private PasswordField nuovaPasswordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private Region barraAnimata;

    // Questo deve corrispondere al fx:id nel FXML
    @FXML private Button btnHome;

    private final LoginController loginController = new LoginController();

    @FXML
    public void initialize() {
        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
    }

    // --- LOGICA CAMBIO PASSWORD ---
    @FXML
    public void handleCambiaPassword(ActionEvent event) {
        // .trim() è fondamentale: un solo spazio invisibile nel DB rompe il match
        String u = usernameField.getText().trim();
        String p = nuovaPasswordField.getText();
        String c = confermaPasswordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            AlertUtils.mostraErrore("Compila tutti i campi.");
            return;
        }
        if (!p.equals(c)) {
            AlertUtils.mostraErrore("Le password non coincidono!");
            return;
        }

        // Prova a chiamare direttamente il DAO per testare se il problema è nel LoginController
        try {
            com.sneakup.model.dao.db.UtenteDAOJDBC utenteDAO = new com.sneakup.model.dao.db.UtenteDAOJDBC();
            com.sneakup.model.domain.Utente utente = utenteDAO.recuperaDatiUtente(u);

            if (utente != null) {
                utente.setPassword(p);
                utenteDAO.aggiornaUtente(utente); // Assicurati che aggiornaUtente faccia l'UPDATE nel DB

                AlertUtils.mostraSuccesso("Password aggiornata nel database! Ora puoi accedere.");
                tornaAlLogin(event);
            } else {
                // Se arrivi qui, significa che "u" non esiste né come username né come email nel DB
                AlertUtils.mostraErrore("Username o Email '" + u + "' non trovata nel sistema.");
            }
        } catch (Exception ex) {
            AlertUtils.mostraErrore("Errore tecnico: " + ex.getMessage());
        }
    }

    // --- NAVIGAZIONE HOME (SDOPPIATA PER FAR FUNZIONARE FXML) ---

    // Metodo chiamato dal LOGO (onMouseClicked)
    @FXML
    public void handleReloadHomeMouse(MouseEvent event) {
        vaiAlBenvenuto(event);
    }

    // Metodo chiamato dal BOTTONE (onAction)
    @FXML
    public void handleReloadHome(ActionEvent event) {
        vaiAlBenvenuto(event);
    }

    // --- NAVIGAZIONE ANNULLA / LOGIN ---

    @FXML
    public void handleAnnulla(ActionEvent event) {
        tornaAlLogin(event);
    }

    // Metodo chiamato dal bottone "Annulla" se usa onAction="#tornaAlLogin"
    @FXML
    public void tornaAlLogin(ActionEvent event) {
        cambiaPagina("/com/sneakup/view/Login.fxml", event);
    }

    // Metodo chiamato se usi il mouse su scritte/icone
    @FXML
    public void tornaAlLogin(MouseEvent event) {
        cambiaPagina("/com/sneakup/view/Login.fxml", event);
    }

    // Metodo per il bottone Login nella barra in alto
    @FXML
    private void handleLoginGenerico(ActionEvent event) {
        cambiaPagina("/com/sneakup/view/Login.fxml", event);
    }

    // --- MENU VARI ---
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            // Passiamo la provenienza di default (Benvenuto)
            CarrelloGUIController ctrl = loader.getController();
            ctrl.setProvenienza("/com/sneakup/view/RecuperoPassword.fxml", null, null, null, null,null);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore Carrello: " + e.getMessage()).showAndWait();
        }
    }

    @FXML public void handleStatoOrdine(ActionEvent event) { AlertUtils.mostraInfo("Non disponibile."); }
    @FXML public void handlePreferiti(ActionEvent event) { AlertUtils.mostraInfo("Non disponibile."); }

    // --- ANIMAZIONI ---
    @FXML
    public void mostraEmuoviBarra(MouseEvent event) {
        if (barraAnimata == null) return;
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }
    @FXML public void nascondiBarra(MouseEvent event) { if(barraAnimata!=null) barraAnimata.setOpacity(0.0); }
    @FXML public void animazioneEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.1); }
    @FXML public void animazioneEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML public void animazioneEntraBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.05); }
    @FXML public void animazioneEsceBottone(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }
    @FXML public void iconaEntra(MouseEvent e) { zoom((Node)e.getSource(), 1.2); }
    @FXML public void iconaEsce(MouseEvent e) { zoom((Node)e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    // --- HELPER NAVIGAZIONE ---
    private void vaiAlBenvenuto(java.util.EventObject event) {
        cambiaPagina("/com/sneakup/view/Benvenuto.fxml", event);
    }

    private void cambiaPagina(String fxml, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = null;
            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else if (usernameField.getScene() != null) {
                stage = (Stage) usernameField.getScene().getWindow();
            }
            if (stage != null) {
                boolean max = stage.isMaximized();
                stage.setScene(new Scene(root));
                stage.setMaximized(max);
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.mostraErrore("Errore navigazione: " + e.getMessage());
        }
    }
}