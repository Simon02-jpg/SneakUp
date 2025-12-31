package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Utente;
import java.sql.*;

public class UtenteDAOJDBC {

    private static final String URL = "jdbc:mysql://localhost:3306/sneakup_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public Utente recuperaDatiUtente(String identificativo) throws SneakUpException {
        // Usiamo i nomi esatti delle colonne del tuo SQL: USERNAME e EMAIL
        String query = "SELECT * FROM UTENTE WHERE USERNAME = ? OR EMAIL = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, identificativo);
            ps.setString(2, identificativo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // ATTENZIONE: i nomi dentro rs.getString devono essere MAIUSCOLI come nel tuo SQL
                    return new Utente(
                            rs.getString("USERNAME"),
                            rs.getString("EMAIL"),
                            rs.getString("PASSWORD"),
                            rs.getString("INDIRIZZO"),
                            rs.getString("CITTA"),
                            rs.getString("CAP"),
                            rs.getString("NUMERO_CARTA"),
                            rs.getString("SCADENZA_CARTA"),
                            rs.getString("CVV")
                    );
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB: " + e.getMessage());
        }
        return null; // Ritorna null se non trova nulla
    }

    public void aggiornaUtente(Utente u) throws SneakUpException {
        // Query aggiornata con i nomi colonne corretti del tuo database.sql
        String query = "UPDATE UTENTE SET PASSWORD=?, INDIRIZZO=?, CITTA=?, CAP=?, NUMERO_CARTA=?, SCADENZA_CARTA=?, CVV=? WHERE USERNAME=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, u.getPassword());
            ps.setString(2, u.getIndirizzo());
            ps.setString(3, u.getCitta());
            ps.setString(4, u.getCap());
            ps.setString(5, u.getNumeroCarta());
            ps.setString(6, u.getScadenzaCarta());
            ps.setString(7, u.getCvv());
            ps.setString(8, u.getUsername());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SneakUpException("Nessun utente aggiornato. Verifica lo username.");
            }

        } catch (SQLException e) {
            throw new SneakUpException("Errore durante l'aggiornamento: " + e.getMessage());
        }
    }
}