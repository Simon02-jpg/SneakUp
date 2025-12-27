package com.sneakup.model.dao.db;

import com.sneakup.model.dao.OrdineDAO;
import com.sneakup.model.domain.Ordine;
import com.sneakup.exception.SneakUpException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOJDBC implements OrdineDAO {
    private final String url = "jdbc:mysql://localhost:3306/sneakup_db";
    private final String user = "root";
    private final String pass = "root"; // Usa la tua password

    @Override
    public void salvaOrdine(Ordine ordine) throws SneakUpException {
        String query = "INSERT INTO ORDINI (idUtente, totale, indirizzo) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, 2); // ID del cliente (client) nel tuo database.sql
            st.setDouble(2, ordine.getTotalePagato()); // Usato getTotalePagato() della tua classe
            st.setString(3, ordine.getIndirizzoSpedizione());

            st.executeUpdate();
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB Ordine", e);
        }
    }

    @Override
    public List<Ordine> getAllOrdini() throws SneakUpException {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ORDINI";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                // Qui dovresti mappare i risultati in oggetti Ordine
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore lettura ordini", e);
        }
        return lista;
    }
}