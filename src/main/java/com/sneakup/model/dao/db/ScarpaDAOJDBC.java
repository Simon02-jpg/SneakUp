package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.util.DBConnection; // Import della tua nuova utility
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScarpaDAOJDBC implements ScarpaDAO {

    private static final Logger logger = Logger.getLogger(ScarpaDAOJDBC.class.getName());

    // Il costruttore ora non ha più bisogno di caricare URL, User e Password dal file properties
    // perché se ne occupa centralmente la classe DBConnection.
    public ScarpaDAOJDBC() {}

    // Metodi placeholder (implementali se necessario)
    public boolean updatePassword(String u, String p) { return false; }
    public boolean registraNuovoUtente(String u, String p, String n, String c, String e) { return false; }
    @Override public void addScarpa(Scarpa s) throws SneakUpException {}
    @Override public List<Scarpa> getAllScarpe() throws SneakUpException { return new ArrayList<>(); }
    @Override public void deleteScarpa(int id) throws SneakUpException {}
    @Override public void updateScarpa(Scarpa s) throws SneakUpException {}
    @Override public List<Recensione> getRecensioniPerScarpa(int id) throws SneakUpException { return new ArrayList<>(); }
    @Override public void aggiungiRecensione(int id, Recensione r) throws SneakUpException {}

    // --- RICERCA FILTRATA ---
    public List<Scarpa> cercaScarpe(String keyword) throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        String query = "SELECT * FROM SCARPE WHERE modello LIKE ? OR marca LIKE ?";

        // Sostituito DriverManager con DBConnection.getConnection()
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);

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

    // --- METODO HELPER PER EVITARE RIPETIZIONI (Mapping) ---
    private Scarpa mappaScarpa(ResultSet rs) throws SQLException {
        Scarpa s = new Scarpa();
        s.setId(rs.getInt("idSCARPA"));
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