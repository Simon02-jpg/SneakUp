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
            logger.log(Level.SEVERE, "Errore caricamento config", ex);
        }
    }

    // ... (Mantieni qui i metodi updatePassword e registraNuovoUtente che avevi già) ...
    public boolean updatePassword(String username, String nuovaPassword) {
        // ... (codice invariato) ...
        return false; // placeholder per brevità, tieni il tuo codice originale
    }
    public boolean registraNuovoUtente(String username, String password, String nome, String cognome, String email) {
        // ... (codice invariato) ...
        return false; // placeholder
    }

    @Override
    public void addScarpa(Scarpa scarpa) throws SneakUpException {
        // ... (Mantieni il tuo codice originale) ...
    }

    @Override
    public List<Scarpa> getAllScarpe() throws SneakUpException {
        // ... (Mantieni il tuo codice originale) ...
        return new ArrayList<>(); // placeholder
    }

    @Override
    public void deleteScarpa(int id) throws SneakUpException {
        // ... (Mantieni il tuo codice originale) ...
    }

    @Override
    public void updateScarpa(Scarpa s) throws SneakUpException {
        // ... (Mantieni il tuo codice originale) ...
    }

    @Override
    public List<Recensione> getRecensioniPerScarpa(int idScarpa) throws SneakUpException {
        // ... (Mantieni il tuo codice originale) ...
        return new ArrayList<>(); // placeholder
    }

    @Override
    public void aggiungiRecensione(int id, Recensione r) throws SneakUpException {
        // ... (Mantieni il tuo codice originale) ...
    }

    // --- NUOVO METODO PER LA RICERCA ---
    public List<Scarpa> cercaScarpe(String keyword) throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        // Cerca sia nel modello che nella marca (es. "Nike" o "Air Max")
        String query = "SELECT idSCARPA, modello, marca, categoria, taglia, prezzo, quantita FROM SCARPE WHERE modello LIKE ? OR marca LIKE ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query)) {

            // %parola% permette di trovare "Air" dentro "Nike Air Max"
            String searchPattern = "%" + keyword + "%";
            st.setString(1, searchPattern);
            st.setString(2, searchPattern);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Scarpa s = new Scarpa();
                    int id = rs.getInt("idSCARPA");
                    s.setId(id);
                    s.setModello(rs.getString("modello"));
                    s.setMarca(rs.getString("marca"));
                    s.setCategoria(rs.getString("categoria"));
                    s.setTaglia(rs.getDouble("taglia"));
                    s.setPrezzo(rs.getDouble("prezzo"));
                    s.setQuantitaDisponibile(rs.getInt("quantita"));

                    // Carica anche le recensioni se necessario (opzionale per la lista veloce)
                    // s.getRecensioni().addAll(getRecensioniPerScarpa(id));

                    lista.add(s);
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore durante la ricerca nel DB", e);
        }
        return lista;
    }
}