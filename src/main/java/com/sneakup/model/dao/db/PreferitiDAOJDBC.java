package com.sneakup.model.dao.db;

import com.sneakup.model.domain.Scarpa;
import com.sneakup.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreferitiDAOJDBC {

    public void salva(String username, int idScarpa) {
        String sql = "INSERT INTO PREFERITI (utente, id_scarpa) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, idScarpa);
            stmt.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            // Gestione silenziosa: il prodotto è già tra i preferiti
            System.out.println("Nota: Prodotto " + idScarpa + " già nei preferiti di " + username);
        } catch (SQLException e) {
            System.err.println("Errore salvataggio preferito: " + e.getMessage());
        }
    }

    public void rimuovi(String username, int idScarpa) {
        String sql = "DELETE FROM PREFERITI WHERE utente = ? AND id_scarpa = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, idScarpa);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore rimozione preferito: " + e.getMessage());
        }
    }

    public List<Scarpa> caricaPreferiti(String username) {
        List<Scarpa> lista = new ArrayList<>();
        // Query con Join per ottenere l'oggetto Scarpa completo
        String sql = "SELECT s.* FROM SCARPE s JOIN PREFERITI p ON s.idSCARPA = p.id_scarpa WHERE p.utente = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Scarpa s = new Scarpa();
                    s.setId(rs.getInt("idSCARPA"));
                    s.setModello(rs.getString("modello"));
                    s.setMarca(rs.getString("marca"));
                    s.setCategoria(rs.getString("categoria"));
                    s.setGenere(rs.getString("genere"));
                    s.setPrezzo(rs.getDouble("prezzo"));
                    s.setUrlImmagine(rs.getString("url_immagine"));
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento preferiti: " + e.getMessage());
        }
        return lista;
    }
}