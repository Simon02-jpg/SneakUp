package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScarpaDAOJDBC implements ScarpaDAO {

    private static final Logger logger = Logger.getLogger(ScarpaDAOJDBC.class.getName());
    private String url;
    private String user;
    private String password;

    public ScarpaDAOJDBC() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                prop.load(input);
                this.url = prop.getProperty("db.url");
                this.user = prop.getProperty("db.user");
                this.password = prop.getProperty("db.password");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Errore config", ex);
        }
    }

    // Metodi placeholder per brevità (mantieni i tuoi originali se li usi)
    public boolean updatePassword(String u, String p) { return false; }
    public boolean registraNuovoUtente(String u, String p, String n, String c, String e) { return false; }
    @Override public void addScarpa(Scarpa s) throws SneakUpException {}
    @Override public List<Scarpa> getAllScarpe() throws SneakUpException { return new ArrayList<>(); }
    @Override public void deleteScarpa(int id) throws SneakUpException {}
    @Override public void updateScarpa(Scarpa s) throws SneakUpException {}
    @Override public List<Recensione> getRecensioniPerScarpa(int id) throws SneakUpException { return new ArrayList<>(); }
    @Override public void aggiungiRecensione(int id, Recensione r) throws SneakUpException {}

    // --- QUERY AGGIORNATA ---
    public List<Scarpa> cercaScarpe(String keyword) throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        // Selezioniamo TUTTO (*) inclusa la nuova colonna GENERE
        String query = "SELECT * FROM SCARPE WHERE modello LIKE ? OR marca LIKE ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Scarpa s = new Scarpa();
                    s.setId(rs.getInt("idSCARPA"));
                    s.setModello(rs.getString("modello"));
                    s.setMarca(rs.getString("marca"));
                    s.setCategoria(rs.getString("categoria"));

                    // --- PUNTO CRUCIALE: LEGGERE IL GENERE ---
                    s.setGenere(rs.getString("genere"));

                    s.setTaglia(rs.getDouble("taglia"));
                    s.setPrezzo(rs.getDouble("prezzo"));
                    s.setQuantitaDisponibile(rs.getInt("quantita"));
                    s.setDescrizione(rs.getString("descrizione"));
                    s.setUrlImmagine(rs.getString("url_immagine"));

                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB", e);
        }
        return lista;
    }
}