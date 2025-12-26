package com.sneakup.view;

import com.sneakup.controller.VisualizzaScarpeController;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Carrello;
import com.sneakup.model.domain.Scarpa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class HomeClienteGUIController {

    @FXML private TableView<Scarpa> tabellaScarpe;
    @FXML private TableColumn<Scarpa, Void> colonnaAzioni;

    private final VisualizzaScarpeController logicController = new VisualizzaScarpeController();

    // 1. Variabile per contenere il carrello ricevuto dal Login
    private Carrello carrello;

    // 2. Setter per ricevere il carrello
    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
    }

    @FXML
    public void initialize() {
        caricaDati();
        aggiungiBottoniAllaTabella();
    }

    private void caricaDati() {
        try {
            List<Scarpa> lista = logicController.getTutteLeScarpe();
            ObservableList<Scarpa> dati = FXCollections.observableArrayList(lista);
            tabellaScarpe.setItems(dati);
        } catch (SneakUpException e) {
            mostraAlert("Errore", "Impossibile caricare il catalogo.");
        }
    }

    private void aggiungiBottoniAllaTabella() {
        Callback<TableColumn<Scarpa, Void>, TableCell<Scarpa, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Scarpa, Void> call(final TableColumn<Scarpa, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Aggiungi");

                    {
                        btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                        btn.setOnAction((ActionEvent event) -> {
                            Scarpa scarpaScelta = getTableView().getItems().get(getIndex());

                            // MODIFICA QUI: Usa this.carrello invece di getInstance()
                            if (carrello != null) {
                                carrello.aggiungiScarpa(scarpaScelta);
                                mostraAlert("Carrello", "Aggiunto: " + scarpaScelta.getModello());
                            } else {
                                mostraAlert("Errore", "Carrello non inizializzato!");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colonnaAzioni.setCellFactory(cellFactory);
    }

    // MODIFICA QUI: Passaggio del carrello alla schermata successiva
    @FXML
    private void vaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/VisualizzaCarrello.fxml"));
            Parent root = loader.load();

            // Passa il carrello al controller del carrello
            CarrelloGUIController controller = loader.getController();
            controller.setCarrello(this.carrello);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        // Al logout il carrello si perde (corretto), si torna al login
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/sneakup/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}