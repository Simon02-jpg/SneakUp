package com.sneakup.view;

import com.sneakup.controller.LoginController;
import com.sneakup.model.domain.Carrello;
import com.sneakup.model.domain.Ruolo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginGUIController {

    @FXML private TextField usernameField; // Nome corretto
    @FXML private PasswordField passwordField; // Nome corretto

    private final LoginController loginController = new LoginController();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText(); // Corretto
        String password = passwordField.getText(); // Corretto

        try {
            Ruolo ruolo = loginController.verificaLogin(username, password);

            if (ruolo == null) {
                mostraAlert("Errore", "Credenziali errate.");
                return;
            }

            if (ruolo == Ruolo.VENDITORE) {
                cambiaScena(event, "/com/sneakup/view/MenuPrincipale.fxml");
            } else if (ruolo == Ruolo.CLIENTE) {
                apriHomeCliente(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostraAlert("Errore", "Errore tecnico durante il login.");
        }
    }

    private void apriHomeCliente(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/HomeCliente.fxml"));
        Parent root = loader.load();
        HomeClienteGUIController homeController = loader.getController();
        homeController.setCarrello(new Carrello());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void cambiaScena(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(msg);
        alert.showAndWait();
    }


}