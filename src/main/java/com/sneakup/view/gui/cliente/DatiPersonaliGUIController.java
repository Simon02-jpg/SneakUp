package com.sneakup.view.gui.cliente;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.Sessione;
import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import com.sneakup.util.AlertUtils; // Assicurati di usare la tua classe utility se presente
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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

    // --- ELEMENTI HEADER DINAMICI (Sincronizzati con Benvenuto) ---
    @FXML private Button btnLogin;
    @FXML private Label lblUser;
    @FXML private Region barraAnimata;
    @FXML private Button btnHome, btnCarrello, btnStato, btnPreferiti;

    // Campi del form
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField nuovaPasswordField;
    @FXML private TextField indirizzoField;
    @FXML private TextField cittaField;
    @FXML private TextField capField;
    @FXML private TextField cartaField;
    @FXML private TextField scadenzaField;
    @FXML private TextField cvvField;
    private final UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();
    private String passwordAttuale = "";

    @FXML
    public void initialize() {
        // 1. Gestione Header (Login vs Nome Utente)
        if (Sessione.getInstance().isLoggato()) {
            if (btnLogin != null) {
                btnLogin.setVisible(false);
                btnLogin.setManaged(false);
            }
            if (lblUser != null) {
                lblUser.setText("CIAO, " + Sessione.getInstance().getUsername().toUpperCase());
                lblUser.setVisible(true);
                lblUser.setManaged(true);
            }
            // Carica i dati dell'utente nei campi del form
            caricaDatiDalDB(Sessione.getInstance().getUsername());
        }

        // 2. Rimuovi il focus dai bottoni della navbar per evitare l'effetto grigio selezione
        btnHome.setFocusTraversable(false);
        btnCarrello.setFocusTraversable(false);
        btnStato.setFocusTraversable(false);
        btnPreferiti.setFocusTraversable(false);

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
        } catch (SneakUpException e) {
            mostraAlert(Alert.AlertType.ERROR, "Errore DB", "Impossibile caricare i dati.");
        }
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
            // Assicurati che il metodo aggiornaUtente nel DAO gestisca tutti i campi
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler eliminare il tuo account? L'operazione è irreversibile.", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Elimina Profilo");
        alert.setHeaderText("ATTENZIONE");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                utenteDAO.eliminaUtente(Sessione.getInstance().getUsername());
                Sessione.getInstance().logout();
                mostraAlert(Alert.AlertType.INFORMATION, "Account Eliminato", "Ci dispiace vederti andare via.");
                handleReloadHome();
            } catch (SneakUpException e) {
                mostraAlert(Alert.AlertType.ERROR, "Errore", e.getMessage());
            }
        }
    }

    // --- NAVIGAZIONE ---

    @FXML private void tornaIndietro(ActionEvent event) { navigaVerso("/com/sneakup/view/AreaPersonale.fxml", event); }
    @FXML private void handleReloadHome() { navigaVerso("/com/sneakup/view/Benvenuto.fxml", null); }
    @FXML private void handleReloadHome(MouseEvent event) { navigaVerso("/com/sneakup/view/Benvenuto.fxml", event); }
    @FXML private void handleLoginGenerico(ActionEvent event) { navigaVerso("/com/sneakup/view/Login.fxml", event); }

    // Rinfresca la pagina corrente
    @FXML private void handleVaiAreaPersonale(MouseEvent event) { navigaVerso("/com/sneakup/view/DatiPersonali.fxml", event); }

    private void navigaVerso(String fxmlPath, java.util.EventObject event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage;
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                // Fallback se chiamato senza evento (es. dall'Alert)
                stage = (Stage) usernameField.getScene().getWindow();
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- ANIMAZIONI E INTERFACCIA ---

    @FXML private void sottolineaUser(MouseEvent event) { lblUser.setUnderline(true); }
    @FXML private void ripristinaUser(MouseEvent event) { lblUser.setUnderline(false); }
    @FXML private void iconaEntra(MouseEvent e) { zoom((Node) e.getSource(), 1.1); }
    @FXML private void iconaEsce(MouseEvent e) { zoom((Node) e.getSource(), 1.0); }

    private void zoom(Node n, double s) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), n);
        st.setToX(s); st.setToY(s); st.play();
    }

    @FXML
    private void mostraEmuoviBarra(MouseEvent event) {
        Node source = (Node) event.getSource();
        Bounds b = source.localToScene(source.getBoundsInLocal());
        Parent p = barraAnimata.getParent();
        javafx.geometry.Point2D loc = p.sceneToLocal(b.getMinX(), b.getMinY());

        barraAnimata.setLayoutX(loc.getX());
        barraAnimata.setPrefWidth(b.getWidth());
        barraAnimata.setOpacity(1.0);
    }

    @FXML private void nascondiBarra(MouseEvent event) { if (barraAnimata != null) barraAnimata.setOpacity(0.0); }
    @FXML private void handleCarrello(ActionEvent event) { mostraAlert(Alert.AlertType.INFORMATION, "Info", "Area Carrello"); }
    @FXML private void handleStatoOrdine(ActionEvent event) { mostraAlert(Alert.AlertType.INFORMATION, "Info", "Area Ordini"); }
    @FXML private void handlePreferiti(ActionEvent event) { mostraAlert(Alert.AlertType.INFORMATION, "Info", "Area Preferiti"); }
    private void mostraAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(text); alert.showAndWait();
    }
}