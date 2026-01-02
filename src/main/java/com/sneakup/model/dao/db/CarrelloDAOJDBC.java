package com.sneakup.model.dao.db;

import com.sneakup.model.domain.Scarpa;
import com.sneakup.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrelloDAOJDBC {

    public List<Scarpa> caricaCarrello(String username) {
        List<Scarpa> lista = new ArrayList<>();
        // Query corretta: recupera i dettagli delle scarpe associate all'utente nel carrello
        String query = "SELECT s.* FROM SCARPE s JOIN carrello c ON s.idSCARPA = c.id_scarpa WHERE c.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Scarpa s = new Scarpa();
                    s.setId(rs.getInt("idSCARPA"));
                    s.setModello(rs.getString("modello"));
                    s.setPrezzo(rs.getDouble("prezzo"));
                    s.setMarca(rs.getString("marca"));
                    s.setGenere(rs.getString("genere"));
                    s.setCategoria(rs.getString("categoria"));
                    s.setUrlImmagine(rs.getString("url_immagine"));
                    s.setDescrizione(rs.getString("descrizione"));
                    s.setTaglia(rs.getDouble("taglia"));
                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento carrello per " + username + ": " + e.getMessage());
        }
        return lista;
    }

    public void salva(String username, int idScarpa) {
        String query = "INSERT INTO carrello (username, id_scarpa) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, idScarpa);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore salvataggio carrello: " + e.getMessage());
        }
    }

    public void rimuovi(String username, int idScarpa) {
        // Rimuove solo una singola istanza della scarpa (importante per MySQL se ci sono duplicati)
        String query = "DELETE FROM carrello WHERE username = ? AND id_scarpa = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, idScarpa);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore rimozione carrello: " + e.getMessage());
        }
    }

    public void svuota(String username) {
        String query = "DELETE FROM carrello WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore svuotamento carrello: " + e.getMessage());
        }
    }
}