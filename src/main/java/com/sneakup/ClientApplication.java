package com.sneakup;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // Opzionale se vuoi togliere i bordi, ma per ora teniamoli

import java.io.IOException;

public class ClientApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/com/sneakup/view/Benvenuto.fxml")); // O Login.fxml
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("SneakUp - Store");
        stage.setScene(scene);

        // --- MODIFICA PER FULL SCREEN ISTANTANEO ---
        // 1. Recuperiamo le dimensioni dello schermo principale
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // 2. Impostiamo manualmente coordinate e grandezza
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        // 3. Diciamo comunque che è massimizzata (per gestire l'icona 'restore' in alto a destra)
        stage.setMaximized(true);
        // ------------------------------------------

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}