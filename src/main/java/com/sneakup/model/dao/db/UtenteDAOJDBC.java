package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Utente;
import com.sneakup.util.DBConnection; // Import della tua utility centralizzata

import java.sql.*;

public class UtenteDAOJDBC {

    public Utente recuperaDatiUtente(String identificativo) throws SneakUpException {
        // Cerca l'utente tramite username o email
        String query = "SELECT * FROM Utente WHERE username = ? OR email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, identificativo);
            ps.setString(2, identificativo);

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
            throw new SneakUpException("Errore durante il recupero utente: " + e.getMessage());
        }
        return null;
    }

    public void aggiornaUtente(Utente u) throws SneakUpException {
        // Aggiorniamo i dati del profilo e della carta di credito
        String query = "UPDATE Utente SET email=?, password=?, indirizzo=?, citta=?, cap=?, numero_carta=?, scadenza_carta=?, cvv=? WHERE username=?";

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
        String query = "DELETE FROM Utente WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new SneakUpException("Impossibile eliminare il profilo: " + e.getMessage());
        }
    }

    public void salvaNuovoUtente(Utente u) throws SneakUpException {
        // Inserimento iniziale (Registrazione)
        String query = "INSERT INTO UTENTE (USERNAME, PASSWORD, ROLE, EMAIL) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setInt(3, 1); // 1 = Ruolo Cliente (Default)
            ps.setString(4, u.getEmail());

            ps.executeUpdate();
            System.out.println("DEBUG: Registrazione completata con successo nel DB");
        } catch (SQLException e) {
            throw new SneakUpException("Errore durante la registrazione: " + e.getMessage());
        }
    }
}