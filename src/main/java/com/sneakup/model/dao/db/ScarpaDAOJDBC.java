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

    @Override
    public void addScarpa(Scarpa scarpa) throws SneakUpException {
        // Corretto: Nome tabella SCARPE e rimosso categoria (non presente nel tuo SQL per l'insert)
        String query = "INSERT INTO SCARPE (modello, marca, categoria, taglia, prezzo, quantita) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, scarpa.getModello());
            st.setString(2, scarpa.getMarca());
            st.setString(3, scarpa.getCategoria());
            st.setDouble(4, scarpa.getTaglia());
            st.setDouble(5, scarpa.getPrezzo());
            st.setInt(6, scarpa.getQuantitaDisponibile());
            st.executeUpdate();
            try (ResultSet gk = st.getGeneratedKeys()) {
                if (gk.next()) scarpa.setId(gk.getInt(1));
            }
        } catch (SQLException e) { throw new SneakUpException("Errore inserimento", e); }
    }

    @Override
    public List<Scarpa> getAllScarpe() throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        // Query corretta secondo il tuo database.sql
        String query = "SELECT idSCARPA, modello, marca, categoria, taglia, prezzo, quantita FROM SCARPE";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Scarpa s = new Scarpa();
                int id = rs.getInt("idSCARPA"); // idSCARPA come nel database.sql
                s.setId(id);
                s.setModello(rs.getString("modello"));
                s.setMarca(rs.getString("marca"));
                s.setCategoria(rs.getString("categoria"));
                s.setTaglia(rs.getDouble("taglia"));
                s.setPrezzo(rs.getDouble("prezzo"));
                s.setQuantitaDisponibile(rs.getInt("quantita"));

                // Carica le recensioni associate
                s.getRecensioni().addAll(getRecensioniPerScarpa(id));
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore lettura catalogo", e);
        }
        return lista;
    }

    @Override
    public void deleteScarpa(int id) throws SneakUpException {
        // Corretto: Nome tabella SCARPE e colonna idSCARPA
        String query = "DELETE FROM SCARPE WHERE idSCARPA = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { throw new SneakUpException("Errore eliminazione", e); }
    }

    @Override
    public void updateScarpa(Scarpa s) throws SneakUpException {
        // Corretto: Nome tabella SCARPE e colonna idSCARPA
        String query = "UPDATE SCARPE SET modello=?, marca=?, categoria=?, taglia=?, prezzo=?, quantita=? WHERE idSCARPA=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, s.getModello());
            st.setString(2, s.getMarca());
            st.setString(3, s.getCategoria());
            st.setDouble(4, s.getTaglia());
            st.setDouble(5, s.getPrezzo());
            st.setInt(6, s.getQuantitaDisponibile());
            st.setInt(7, s.getId());
            st.executeUpdate();
        } catch (SQLException e) { throw new SneakUpException("Errore aggiornamento", e); }
    }

    @Override
    public List<Recensione> getRecensioniPerScarpa(int idScarpa) throws SneakUpException {
        List<Recensione> recensioni = new ArrayList<>();
        // Corretto: idSCARPA come chiave esterna nel database.sql
        String query = "SELECT autore, voto, testo FROM RECENSIONI WHERE idSCARPA = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, idScarpa);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(new Recensione(rs.getString("autore"), rs.getInt("voto"), rs.getString("testo")));
                }
            }
        } catch (SQLException e) { throw new SneakUpException("Errore recupero recensioni", e); }
        return recensioni;
    }

    @Override
    public void aggiungiRecensione(int id, Recensione r) throws SneakUpException {
        // Corretto: idSCARPA come chiave esterna nel database.sql
        String query = "INSERT INTO RECENSIONI (idSCARPA, autore, voto, testo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, id);
            st.setString(2, r.getAutore());
            st.setInt(3, r.getVoto());
            st.setString(4, r.getTesto());
            st.executeUpdate();
        } catch (SQLException e) { throw new SneakUpException("Errore salvataggio recensione", e); }
    }
}