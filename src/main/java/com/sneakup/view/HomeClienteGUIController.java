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
import javafx.scene.control.cell.PropertyValueFactory; // AGGIUNTO
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class HomeClienteGUIController {

    // CORREZIONE: Assicurati che questi fx:id corrispondano a HomeCliente.fxml
    @FXML private TableView<Scarpa> tabellaScarpe;
    @FXML private TableColumn<Scarpa, String> colonnaModello;
    @FXML private TableColumn<Scarpa, String> colonnaMarca;
    @FXML private TableColumn<Scarpa, Double> colonnaPrezzo;
    @FXML private TableColumn<Scarpa, Void> colonnaAzioni;

    private final VisualizzaScarpeController logicController = new VisualizzaScarpeController();
    private Carrello carrello;

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
    }

    @FXML
    public void initialize() {
        // CORREZIONE: Collega i dati alle colonne (altrimenti la tabella resta vuota o crasha)
        // Se hai già messo i PropertyValueFactory nel file FXML, puoi commentare queste 3 righe
        colonnaModello.setCellValueFactory(new PropertyValueFactory<>("modello"));
        colonnaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colonnaPrezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));

        caricaDati();
        aggiungiBottoniAllaTabella();
    }

    private void caricaDati() {
        try {
            List<Scarpa> lista = logicController.getTutteLeScarpe();
            tabellaScarpe.setItems(FXCollections.observableArrayList(lista));
        } catch (SneakUpException e) {
            mostraAlert("Errore", "Impossibile caricare il catalogo dal database.");
        }
    }

    private void aggiungiBottoniAllaTabella() {
        Callback<TableColumn<Scarpa, Void>, TableCell<Scarpa, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Scarpa, Void> call(final TableColumn<Scarpa, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Aggiungi");
                    {
                        btn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
                        btn.setOnAction((ActionEvent event) -> {
                            Scarpa scarpaScelta = getTableView().getItems().get(getIndex());
                            if (carrello != null) {
                                carrello.aggiungiScarpa(scarpaScelta);
                                // Feedback visivo (opzionale: potresti usare una label invece di un alert fastidioso)
                                System.out.println("Aggiunto al carrello: " + scarpaScelta.getModello());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        };
        colonnaAzioni.setCellFactory(cellFactory);
    }

    @FXML
    private void vaiAlCarrello(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/VisualizzaCarrello.fxml"));
            Parent root = loader.load();

            CarrelloGUIController controller = loader.getController();
            controller.setCarrello(this.carrello); // Passa l'oggetto carrello

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
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