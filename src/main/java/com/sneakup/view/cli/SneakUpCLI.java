package com.sneakup.view.cli;

import com.sneakup.controller.VisualizzaScarpeController;
import com.sneakup.model.domain.Scarpa;

import java.util.List;
import java.util.Scanner;

public class SneakUpCLI {

    private VisualizzaScarpeController controller = new VisualizzaScarpeController();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("*** BENVENUTO IN SNEAKUP (CLI VERSION) ***");
        boolean running = true;

        while (running) {
            System.out.println("\nScegli operazione:");
            System.out.println("1. Visualizza Catalogo");
            System.out.println("2. Esci");
            System.out.print("> ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    mostraCatalogo();
                    break;
                case "2":
                    running = false;
                    break;
                default:
                    System.out.println("Comando non valido.");
            }
        }
    }

    private void mostraCatalogo() {
        try {
            // RIUSO LO STESSO CONTROLLER DI JAVAFX!
            List<Scarpa> scarpe = controller.getTutteLeScarpe();
            System.out.println("\n--- CATALOGO ---");
            for (Scarpa s : scarpe) {
                System.out.println(s); // Usa il toString() di Scarpa
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // Main per lanciare la CLI separatamente
    public static void main(String[] args) {
        new SneakUpCLI().start();
    }
}