package com.sneakup.model.dao.db;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Scarpa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScarpaDAOJDBC implements ScarpaDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/sneakup_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Metti la tua pass

    @Override
    public void addScarpa(Scarpa scarpa) throws SneakUpException {
        String query = "INSERT INTO scarpe (modello, marca, categoria, taglia, prezzo, quantita) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, scarpa.getModello());
            st.setString(2, scarpa.getMarca());
            st.setString(3, scarpa.getCategoria());
            st.setDouble(4, scarpa.getTaglia());
            st.setDouble(5, scarpa.getPrezzo());
            st.setInt(6, scarpa.getQuantitaDisponibile());

            st.executeUpdate();

            // Recupera ID generato
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    scarpa.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB inserimento", e);
        }
    }

    @Override
    public List<Scarpa> getAllScarpe() throws SneakUpException {
        List<Scarpa> lista = new ArrayList<>();
        String query = "SELECT * FROM scarpe";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Scarpa s = new Scarpa();
                s.setId(rs.getInt("id"));
                s.setModello(rs.getString("modello"));
                s.setMarca(rs.getString("marca"));
                s.setCategoria(rs.getString("categoria"));
                s.setTaglia(rs.getDouble("taglia"));
                s.setPrezzo(rs.getDouble("prezzo"));
                s.setQuantitaDisponibile(rs.getInt("quantita"));
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB lettura", e);
        }
        return lista;
    }

    // Implementa deleteScarpa e updateScarpa in modo simile...
    @Override
    public void deleteScarpa(int id) throws SneakUpException { /* ... query DELETE ... */ }

    @Override
    public void updateScarpa(Scarpa s) throws SneakUpException { /* ... query UPDATE ... */ }
}