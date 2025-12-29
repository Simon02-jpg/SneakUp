package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Utente;

import java.sql.*;

public class UtenteDAOJDBC {

    // CAMBIA QUI CON I TUOI DATI DEL DB
    private static final String URL = "jdbc:mysql://localhost:3306/sneakUpDB";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public Utente recuperaDatiUtente(String username) throws SneakUpException {
        String query = "SELECT * FROM Utente WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("indirizzo"),
                            rs.getString("citta"),
                            rs.getString("cap"),
                            rs.getString("numero_carta"),
                            rs.getString("scadenza_carta"),
                            rs.getString("cvv")
                    );
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore nel recupero dati utente: " + e.getMessage());
        }
        return null;
    }

    public void aggiornaUtente(Utente u) throws SneakUpException {
        // Aggiorniamo tutto tranne lo username (che è chiave primaria)
        String query = "UPDATE Utente SET email=?, password=?, indirizzo=?, citta=?, cap=?, numero_carta=?, scadenza_carta=?, cvv=? WHERE username=?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
            throw new SneakUpException("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    public void eliminaUtente(String username) throws SneakUpException {
        String query = "DELETE FROM Utente WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new SneakUpException("Impossibile eliminare il profilo: " + e.getMessage());
        }
    }
}