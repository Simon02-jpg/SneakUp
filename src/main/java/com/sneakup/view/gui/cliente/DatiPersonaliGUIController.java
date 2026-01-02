package com.sneakup.view.gui.cliente;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class DatiPersonaliGUIController {

    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti;

    @FXML private TextField usernameField, emailField, indirizzoField, cittaField, capField, cartaField, scadenzaField, cvvField;
    @FXML private PasswordField nuovaPasswordField;

    private final UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();
    private String passwordAttuale = "";

    @FXML
    public void initialize() {
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) { btnLogin.setVisible(false); btnLogin.setManaged(false); }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true);
                lblUser.setManaged(true);
            }
            usernameField.setText(Sessione.getInstance().getUsername());
            caricaDatiDalDB(Sessione.getInstance().getUsername());
        }

        if(btnHome != null) btnHome.setFocusTraversable(false);
        if(btnCarrello != null) btnCarrello.setFocusTraversable(false);
        if(btnStato != null) btnStato.setFocusTraversable(false);
        if(btnPreferiti != null) btnPreferiti.setFocusTraversable(false);

        if (barraAnimata != null) barraAnimata.setOpacity(0.0);
    }

    private void caricaDatiDalDB(String username) {
        try {
            Utente u = utenteDAO.recuperaDatiUtente(username);
            if (u != null) {
                emailField.setText(u.getEmail());
                passwordAttuale = u.getPassword();
                if (u.getIndirizzo() != null) indirizzoField.setText(u.getIndirizzo());
                if (u.getCitta() != null) cittaField.setText(u.getCitta());
                if (u.getCap() != null) capField.setText(u.getCap());
                if (u.getNumeroCarta() != null) cartaField.setText(u.getNumeroCarta());
                if (u.getScadenzaCarta() != null) scadenzaField.setText(u.getScadenzaCarta());
                if (u.getCvv() != null) cvvField.setText(u.getCvv());
            }
        } catch (SneakUpException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSalva(ActionEvent event) {
        String passwordFinale = nuovaPasswordField.getText().isEmpty() ? passwordAttuale : nuovaPasswordField.getText();
        Utente utenteAggiornato = new Utente(
                usernameField.getText(), emailField.getText(), passwordFinale,
                indirizzoField.getText(), cittaField.getText(), capField.getText(),
                cartaField.getText(), scadenzaField.getText(), cvvField.getText()
        );

        try {
            utenteDAO.aggiornaUtente(utenteAggiornato);
            mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Dati salvati correttamente!");
            passwordAttuale = passwordFinale;
            nuovaPasswordField.clear();
        } catch (SneakUpException e) {
            mostraAlert(Alert.AlertType.ERROR, "Errore Salvataggio", e.getMessage());
        }
    }

    @FXML
    private void handleEliminaProfilo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "L'operazione è irreversibile. Eliminare?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                utenteDAO.eliminaUtente(Sessione.getInstance().getUsername());
                Sessione.getInstance().logout();
                navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
            } catch (SneakUpException e) { e.printStackTrace(); }
        }
    }

    // --- NAVIGAZIONE ---

    @FXML
    private void handleReloadHome(ActionEvent event) {
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML
    private void handleReloadHome(MouseEvent event) {
        navigaVerso("/com/sneakup/view/Benvenuto.fxml", event);
    }

    @FXML
    private void handlePreferiti(ActionEvent event) {
        navigaVerso("/com/sneakup/view/Preferiti.fxml", event);
    }

    @FXML
    private void tornaIndietro(ActionEvent event) {
        navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event);
    }

    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }

    // --- METODO CARRELLO CORRETTO ---
    @FXML
    private void handleCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/Carrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController ctrl = loader.getController();

            // CORREZIONE: Aggiunto il sesto parametro 'null' per la Scarpa
            ctrl.setProvenienza(
                    "/com/sneakup/view/DatiPersonali.fxml",
                    null,
                    null,
                    null,
                    null,
                    null  // <--- Parametro mancante aggiunto qui
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Errore Carrello: " + e.getMessage()).showAndWait();
        }
    }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraAlert(Alert.AlertType.INFORMATION, "Info", "In arrivo"); }

    private void navigaVerso(String fxmlPath, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- ANIMAZIONI ---
    @FXML public void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        javafx.geometry.Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());
        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }

    @FXML private void nascondiBarra(MouseEvent event) { if (barraAnimata != null) barraAnimata.setOpacity(0.0); }
    @FXML private void sottolineaUser(MouseEvent event) { lblUser.setUnderline(true); }
    @FXML private void ripristinaUser(MouseEvent event) { lblUser.setUnderline(false); }
    @FXML private void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML private void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }
    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }
    private void mostraAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(text); alert.showAndWait();
    }
}