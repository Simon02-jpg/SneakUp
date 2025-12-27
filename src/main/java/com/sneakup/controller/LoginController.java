package com.sneakup.controller;

import com.sneakup.model.dao.DAOFactory;
import com.sneakup.model.domain.Ruolo;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.InputStream;

public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    /**
     * Verifica le credenziali leggendo direttamente dal Database (HeidiSQL).
     * Risolve l'errore "cannot find symbol" eliminando i MOCK_DB.
     */
    public Ruolo verificaLogin(String username, String password) {
        // Leggiamo le credenziali dal file config.properties
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

        // Query per cercare l'utente nel database (Tabella LOGIN del tuo file database.sql)
        String query = "SELECT ROLE FROM LOGIN WHERE USERNAME = ? AND PASSWORD = ?";

        try {
            // Forza il caricamento del driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int roleId = rs.getInt("ROLE");
                        // 0=Admin(VENDITORE), 1=Gestore(CLIENTE) come da tuo database.sql
                        if (roleId == 0) return Ruolo.VENDITORE;
                        if (roleId == 1) return Ruolo.CLIENTE;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Guarda la console per vedere l'errore specifico
        }
        return null;
    }
}