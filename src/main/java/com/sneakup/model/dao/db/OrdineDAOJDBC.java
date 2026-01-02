package com.sneakup.model.dao.db;

import com.sneakup.model.dao.OrdineDAO;
import com.sneakup.model.domain.Ordine;
import com.sneakup.exception.SneakUpException;
import com.sneakup.util.DBConnection; // Assicurati che l'import sia corretto
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOJDBC implements OrdineDAO {

    @Override
    public void salvaOrdine(Ordine ordine) throws SneakUpException {
        String query = "INSERT INTO ORDINI (idUtente, totale, indirizzo) VALUES (?, ?, ?)";

        // Utilizziamo DBConnection per ottenere la connessione
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {

            // NOTA: Qui l'ID utente è fisso a 2.
            // In un sistema reale useresti Sessione.getInstance().getIdUtente()
            st.setInt(1, 2);
            st.setDouble(2, ordine.getTotalePagato());
            st.setString(3, ordine.getIndirizzoSpedizione());

            st.executeUpdate();
        } catch (SQLException e) {
            throw new SneakUpException("Errore DB Ordine durante il salvataggio", e);
        }
    }

    @Override
    public List<Ordine> getAllOrdini() throws SneakUpException {
        List<Ordine> lista = new ArrayList<>();
        String query = "SELECT * FROM ORDINI";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Ordine ordine = new Ordine();
                ordine.setId(rs.getInt("id"));
                // ordine.setIdUtente(rs.getInt("idUtente"));
                ordine.setTotalePagato(rs.getDouble("totale"));
                ordine.setIndirizzoSpedizione(rs.getString("indirizzo"));
                // ordine.setData(rs.getTimestamp("data_ordine")); // Se hai una colonna data

                lista.add(ordine);
            }
        } catch (SQLException e) {
            throw new SneakUpException("Errore lettura ordini dal database", e);
        }
        return lista;
    }
}