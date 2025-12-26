package com.sneakup.view;

import com.sneakup.controller.LoginController;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Carrello; // Importa Carrello
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
                cambiaScena(event, "/com/sneakup/view/MenuPrincipale.fxml");
            } else {
                // MODIFICA QUI: Invece di cambiaScena generico, usiamo un metodo specifico
                // per passare il carrello
                apriHomeCliente(event);
            }

        } catch (SneakUpException | IOException e) {
            erroreLabel.setText(e.getMessage());
            erroreLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    // NUOVO METODO: Crea il carrello e lo passa alla Home
    private void apriHomeCliente(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/HomeCliente.fxml"));
        Parent root = loader.load();

        // 1. Recupera il controller della Home
        HomeClienteGUIController homeController = loader.getController();

        // 2. Crea un NUOVO carrello e passalo
        Carrello nuovoCarrello = new Carrello();
        homeController.setCarrello(nuovoCarrello);

        // 3. Mostra la scena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }
}