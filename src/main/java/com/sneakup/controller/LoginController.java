package com.sneakup.controller;

import com.sneakup.model.dao.db.ScarpaDAOJDBC; // IMPORTANTE: aggiungi questo import
import com.sneakup.model.domain.Ruolo;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.InputStream;

public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    // SOLUZIONE: Dichiariamo scarpaDAO così il metodo resetPassword può usarlo
    private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();

    public Ruolo verificaLogin(String username, String password) {
        Properties prop = new Properties();
        String url = "";
        String user = "";
        String pass = "";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                prop.load(input);
                url = prop.getProperty("db.url");
                user = prop.getProperty("db.user");
                pass = prop.getProperty("db.password");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore caricamento configurazione login", e);
        }

        String query = "SELECT ROLE FROM LOGIN WHERE USERNAME = ? AND PASSWORD = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int roleId = rs.getInt("ROLE");
                        if (roleId == 0) return Ruolo.VENDITORE;
                        if (roleId == 1) return Ruolo.CLIENTE;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean resetPassword(String username, String nuovaPassword) {
        // Ora scarpaDAO esiste e l'errore sparirà
        return scarpaDAO.updatePassword(username, nuovaPassword);
    }

    public boolean registraCliente(String username, String password, String nome, String cognome, String email) {
        return scarpaDAO.registraNuovoUtente(username, password, nome, cognome, email);
    }

    public boolean registraUtente(String username, String password, String email, Ruolo ruolo) {
        // Qui simulo il salvataggio. In futuro scriverai su file CSV o Database.
        System.out.println("Tentativo registrazione: " + username + " - " + email);

        // Esempio logica fittizia: se l'utente è "admin", fallisce (già esiste)
        if (username.equalsIgnoreCase("admin")) {
            return false;
        }

        // TODO: Qui dovrai aggiungere il codice per scrivere nel file Credenziali.csv
        // Esempio: CSVUtils.scriviNuovoUtente(username, password, email, ruolo);

        return true; // Registrazione riuscita
    }
}