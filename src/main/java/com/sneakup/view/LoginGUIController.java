package com.sneakup.view;

import com.sneakup.controller.LoginController;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Ruolo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginGUIController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label erroreLabel;

    private final LoginController loginController = new LoginController();

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        try {
            Ruolo ruolo = loginController.login(user, pass);

            if (ruolo == Ruolo.VENDITORE) {
                // Se è VENDITORE va al menu di gestione (quello che abbiamo già fatto)
                cambiaScena(event, "/com/sneakup/view/MenuPrincipale.fxml");
            } else {
                // Se è CLIENTE va alla Home Page Cliente (che dobbiamo creare ora!)
                cambiaScena(event, "/com/sneakup/view/HomeCliente.fxml");
            }

        } catch (SneakUpException e) {
            erroreLabel.setText(e.getMessage());
            erroreLabel.setVisible(true);
        }
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            erroreLabel.setText("Errore nel caricamento della pagina: " + fxmlPath);
            erroreLabel.setVisible(true);
        }
    }
}