package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScarpaDAOJDBC implements ScarpaDAO {

    private static final Logger logger = Logger.getLogger(ScarpaDAOJDBC.class.getName());

    public ScarpaDAOJDBC() {}

    // Metodi placeholder
    public boolean updatePassword(String u, String p) { return false; }
    public boolean registraNuovoUtente(String u, String p, String n, String c, String e) { return false; }
    @Override public void addScarpa(Scarpa s) throws SneakUpException {}
    @Override public List<Scarpa> getAllScarpe() throws SneakUpException { return new ArrayList<>(); }
    @Override public void deleteScarpa(int id) throws SneakUpException {}
    @Override public void updateScarpa(Scarpa s) throws SneakUpException {}
    @Override public void aggiungiRecensione(int id, Recensione r) throws SneakUpException {}

    // ==========================================
    // RECUPERO DATI PRODOTTO
    // ==========================================

    /**
     * Recupera una scarpa specifica dato il suo ID.
     * Fondamentale per il Carrello per recuperare il prezzo base.
     */
    public Scarpa getScarpaById(int id) {
        String sql = "SELECT * FROM SCARPE WHERE idSCARPA = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Utilizziamo il metodo di mapping esistente
                    return mappaScarpa(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- RICERCA FILTRATA ---
    public List<Scarpa> cercaScarpe(String keyword) throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        // Se keyword è null o vuota, prendi tutto (logica di default)
        String query = (keyword == null) ? "SELECT * FROM SCARPE" : "SELECT * FROM SCARPE WHERE modello LIKE ? OR marca LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            if (keyword != null) {
                String searchPattern = "%" + keyword + "%";
                st.setString(1, searchPattern);
                st.setString(2, searchPattern);
            }

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(mappaScarpa(rs));
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB durante la ricerca", e);
        }
        return lista;
    }

    // --- RICERCA GLOBALE ---
    public List<Scarpa> cercaPerNome(String testo) throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        String query = "SELECT * FROM SCARPE WHERE modello LIKE ? OR marca LIKE ? OR descrizione LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            String pattern = "%" + testo + "%";
            st.setString(1, pattern);
            st.setString(2, pattern);
            st.setString(3, pattern);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    lista.add(mappaScarpa(rs));
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore durante la ricerca globale", e);
        }
        return lista;
    }

    // ==========================================
    // GESTIONE RECENSIONI E VOTI
    // ==========================================

    // Recupera lista recensioni per una scarpa
    @Override
    public List<Recensione> getRecensioniPerScarpa(int idScarpa) {
        List<Recensione> lista = new ArrayList<>();
        String sql = "SELECT * FROM RECENSIONE WHERE id_scarpa = ? ORDER BY data_inserimento DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idScarpa);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione();
                    r.setId(rs.getInt("id"));
                    r.setIdScarpa(rs.getInt("id_scarpa"));
                    r.setUsername(rs.getString("username_utente"));
                    r.setVoto(rs.getInt("voto"));
                    r.setTesto(rs.getString("testo"));
                    r.setDataInserimento(rs.getTimestamp("data_inserimento"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Aggiunge un voto al DB
    public void aggiungiVoto(int idScarpa, String username, int voto) {
        String sql = "INSERT INTO RECENSIONE (id_scarpa, username_utente, voto) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idScarpa);
            pstmt.setString(2, username);
            pstmt.setInt(3, voto);
            pstmt.executeUpdate();
            System.out.println("Voto salvato correttamente per scarpa ID: " + idScarpa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calcola la media dei voti per una scarpa
    public double getMediaVoti(int idScarpa) {
        String sql = "SELECT AVG(voto) as media FROM RECENSIONE WHERE id_scarpa = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idScarpa);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("media");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Controlla se l'utente ha già votato
    public boolean utenteHaGiaVotato(int idScarpa, String username) {
        String sql = "SELECT count(*) FROM RECENSIONE WHERE id_scarpa = ? AND username_utente = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idScarpa);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================================
    // MAPPING (ResultSet -> Oggetto Java)
    // ==========================================
    private Scarpa mappaScarpa(ResultSet rs) throws SQLException {
        Scarpa s = new Scarpa();
        s.setId(rs.getInt("idSCARPA")); // Deve coincidere con il nome colonna nel DB
        s.setModello(rs.getString("modello"));
        s.setMarca(rs.getString("marca"));
        s.setCategoria(rs.getString("categoria"));
        s.setGenere(rs.getString("genere"));
        s.setTaglia(rs.getDouble("taglia"));
        s.setPrezzo(rs.getDouble("prezzo"));
        s.setQuantitaDisponibile(rs.getInt("quantita"));
        s.setDescrizione(rs.getString("descrizione"));
        s.setUrlImmagine(rs.getString("url_immagine"));
        return s;
    }
}