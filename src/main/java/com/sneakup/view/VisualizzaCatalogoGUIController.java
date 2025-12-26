package com.sneakup.view;

import com.sneakup.controller.VisualizzaScarpeController;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Scarpa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

public class VisualizzaCatalogoGUIController {

    @FXML private TableView<Scarpa> tabellaScarpe;
    @FXML private TableColumn<Scarpa, Void> colonnaAzioni; // Riferimento FXML

    private final VisualizzaScarpeController logicController = new VisualizzaScarpeController();

    @FXML
    public void initialize() {
        setupTabella();
        caricaDati();
    }

    // Configura i bottoni nella tabella
    // Configura i bottoni nella tabella
    private void setupTabella() {
        Callback<TableColumn<Scarpa, Void>, TableCell<Scarpa, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Scarpa, Void> call(final TableColumn<Scarpa, Void> param) {
                return new TableCell<>() {

                    private final Button btnModifica = new Button("Modifica");
                    private final Button btnElimina = new Button("Elimina");
                    private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnModifica, btnElimina);

                    {
                        // Stile Bottone Modifica (Blu/Giallo)
                        btnModifica.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
                        btnModifica.setOnAction(event -> {
                            Scarpa scarpa = getTableView().getItems().get(getIndex());
                            apriModifica(scarpa);
                        });

                        // Stile Bottone Elimina (Rosso)
                        btnElimina.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                        btnElimina.setOnAction(event -> {
                            Scarpa scarpa = getTableView().getItems().get(getIndex());
                            gestisciEliminazione(scarpa);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane); // Mostra entrambi i bottoni
                        }
                    }
                };
            }
        };
        colonnaAzioni.setCellFactory(cellFactory);
    }

    // Metodo per aprire la finestra di modifica
    private void apriModifica(Scarpa scarpa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sneakup/view/InserisciScarpa.fxml"));
            Parent root = loader.load();

            // Ottieni il controller della finestra di inserimento e passagli i dati
            InserisciScarpaGUIController controller = loader.getController();
            controller.setDatiScarpa(scarpa);

            // Cambia scena
            Stage stage = (Stage) tabellaScarpe.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void gestisciEliminazione(Scarpa s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setContentText("Sei sicuro di voler eliminare: " + s.getModello() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                logicController.eliminaScarpa(s.getId()); // Chiama il backend
                caricaDati(); // Ricarica la tabella (fondamentale!)
            } catch (SneakUpException e) {
                e.printStackTrace();
            }
        }
    }

    // ... (metodi caricaDati, handleAggiorna, tornaAlMenu rimangono uguali) ...
    @FXML private void handleAggiorna() { caricaDati(); }

    private void caricaDati() {
        try {
            tabellaScarpe.setItems(FXCollections.observableArrayList(logicController.getTutteLeScarpe()));
        } catch (SneakUpException e) { e.printStackTrace(); }
    }

    @FXML
    private void tornaAlMenu(javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/sneakup/view/MenuPrincipale.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}