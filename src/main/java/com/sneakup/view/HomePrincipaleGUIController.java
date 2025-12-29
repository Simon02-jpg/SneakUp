package com.sneakup.view;

import com.sneakup.model.domain.Carrello;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class HomePrincipaleGUIController {

    @FXML private Label labelSaluto;
    @FXML private Button btnLogin;
    @FXML private ImageView iconaAccount;

    private Carrello carrello;
    private String nomeUtente;

    // --- QUESTI SONO I METODI CHE TI MANCANO ---
    public void setDatiUtente(String nome) {
        this.nomeUtente = nome;
        if (nome != null && !nome.isEmpty()) {
            if (this.labelSaluto != null) {
                this.labelSaluto.setText("Ciao, " + nome);
            }
            if (this.btnLogin != null) {
                this.btnLogin.setVisible(false);
            }
            if (this.iconaAccount != null) {
                this.iconaAccount.setVisible(true);
            }
        }
    }

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
    }
    // -------------------------------------------

    @FXML
    private void apriCatalogoNike(MouseEvent event) {
        vaiAlCatalogo(event, "Nike");
    }

    @FXML
    private void apriCatalogoAdidas(MouseEvent event) {
        vaiAlCatalogo(event, "Adidas");
    }

    @FXML
    private void apriCatalogoPuma(MouseEvent event) {
        vaiAlCatalogo(event, "Puma");
    }

    @FXML
    private void vaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/VisualizzaCarrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController controller = loader.getController();
            if (controller != null) {
                controller.setCarrello(this.carrello);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void vaiAlLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void vaiAlCatalogo(MouseEvent event, String marca) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/HomeCliente.fxml"));
            Parent root = loader.load();

            HomeClienteGUIController controller = loader.getController();
            if (controller != null) {
                controller.setCarrello(this.carrello);
                // Se vuoi filtrare per marca, puoi aggiungere controller.filtraPerMarca(marca);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}