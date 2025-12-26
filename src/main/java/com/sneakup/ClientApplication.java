package com.sneakup;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/com/sneakup/view/Benvenuto.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // Rimuovi le dimensioni fisse (400, 500)

        stage.setTitle("SneakUp - Store");
        stage.setScene(scene);

        // OPZIONE 1: Massimizzato (Finestra grande come lo schermo, si vede la barra Start) -> CONSIGLIATO
        stage.setMaximized(true);

        // OPZIONE 2: Schermo Intero Totale (Nasconde barra Start, si esce con ESC)
        // stage.setFullScreen(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}