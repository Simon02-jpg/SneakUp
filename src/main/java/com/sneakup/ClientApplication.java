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
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("SneakUp - Store");
        stage.setScene(scene);

        // Imposta la finestra massimizzata all'avvio
        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}