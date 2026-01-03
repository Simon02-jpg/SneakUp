package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Utente;
import com.sneakup.model.domain.Ruolo; // Importante
import com.sneakup.util.DBConnection;

import java.sql.*;

public class UtenteDAOJDBC {

    // =========================================================
    // METODI RICHIESTI DAL GESTORE UTENTI
    // =========================================================

    public Utente login(String email, String password) {
        // Aggiungo il controllo anche sull'username se necessario, ma qui lascio email/pass
        String sql = "SELECT * FROM UTENTE WHERE EMAIL = ? AND PASSWORD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mappaUtenteDaResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registra(Utente u) {
        // AGGIUNTO RUOLO ALLA QUERY
        String sql = "INSERT INTO UTENTE (USERNAME, EMAIL, PASSWORD, RUOLO, INDIRIZZO, CITTA, CAP, NUMERO_CARTA, SCADENZA_CARTA, CVV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());

            // Convertiamo l'ENUM in Stringa per il DB
            ps.setString(4, u.getRuolo() != null ? u.getRuolo().name() : Ruolo.CLIENTE.name());

            ps.setString(5, u.getIndirizzo() != null ? u.getIndirizzo() : "");
            ps.setString(6, u.getCitta() != null ? u.getCitta() : "");
            ps.setString(7, u.getCap() != null ? u.getCap() : "");
            ps.setString(8, u.getNumeroCarta() != null ? u.getNumeroCarta() : "");
            ps.setString(9, u.getScadenzaCarta() != null ? u.getScadenzaCarta() : "");
            ps.setString(10, u.getCvv() != null ? u.getCvv() : "");

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Errore registrazione: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // METODI PER AREA PERSONALE
    // =========================================================

    public Utente recuperaDatiUtente(String identificativo) throws SneakUpException {
        String query = "SELECT * FROM UTENTE WHERE USERNAME = ? OR EMAIL = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, identificativo);
            ps.setString(2, identificativo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mappaUtenteDaResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore durante il recupero utente: " + e.getMessage());
        }
        return null;
    }

    public void aggiornaUtente(Utente u) throws SneakUpException {
        // Aggiorniamo i dati. Nota: Di solito il Ruolo non si cambia dal profilo utente standard.
        String query = "UPDATE UTENTE SET EMAIL=?, PASSWORD=?, INDIRIZZO=?, CITTA=?, CAP=?, NUMERO_CARTA=?, SCADENZA_CARTA=?, CVV=? WHERE USERNAME=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getIndirizzo());
            ps.setString(4, u.getCitta());
            ps.setString(5, u.getCap());
            ps.setString(6, u.getNumeroCarta());
            ps.setString(7, u.getScadenzaCarta());
            ps.setString(8, u.getCvv());
            ps.setString(9, u.getUsername());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new SneakUpException("Errore durante il salvataggio delle modifiche: " + e.getMessage());
        }
    }

    public void eliminaUtente(String username) throws SneakUpException {
        String query = "DELETE FROM UTENTE WHERE USERNAME = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new SneakUpException("Impossibile eliminare il profilo: " + e.getMessage());
        }
    }

    // =========================================================
    // METODO DI UTILITÀ PRIVATO
    // =========================================================

    // Serve per non ripetere il codice di creazione Utente in ogni metodo
    private Utente mappaUtenteDaResultSet(ResultSet rs) throws SQLException {
        // Recuperiamo la stringa del ruolo dal DB
        String ruoloStr = rs.getString("RUOLO");
        Ruolo ruoloEnum = Ruolo.CLIENTE; // Default

        if (ruoloStr != null) {
            try {
                ruoloEnum = Ruolo.valueOf(ruoloStr);
            } catch (IllegalArgumentException e) {
                // Se nel DB c'è scritto qualcosa che non matcha l'enum
                ruoloEnum = Ruolo.CLIENTE;
            }
        }

        return new Utente(
                rs.getString("USERNAME"),
                rs.getString("EMAIL"),
                rs.getString("PASSWORD"),
                ruoloEnum, // Passo l'oggetto Ruolo corretto
                rs.getString("INDIRIZZO"),
                rs.getString("CITTA"),
                rs.getString("CAP"),
                rs.getString("NUMERO_CARTA"),
                rs.getString("SCADENZA_CARTA"),
                rs.getString("CVV")
        );
    }
}