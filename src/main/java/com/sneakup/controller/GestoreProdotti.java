package com.sneakup.controller;

import com.sneakup.model.dao.db.ScarpaDAOJDBC;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import java.util.List;

/**
 * CONTROLLER APPLICATIVO (Pattern Control)
 * Gestisce la logica di business e media tra la View e il DAO.
 */
public class GestoreProdotti {

    private final ScarpaDAOJDBC scarpaDAO = new ScarpaDAOJDBC();

    // --- LOGICA DI BUSINESS (Spostata dalla GUI a qui) ---
    public double calcolaPrezzoDinamico(Scarpa s, int taglia, String colore) {
        double prezzo = s.getPrezzo(); // Prezzo base dal DB

        // Regola 1: Taglie grandi costano di più
        if (taglia >= 45) {
            prezzo += 10.0;
        }

        // Regola 2: Edizioni limitate
        if (colore != null) {
            if (colore.contains("Limited")) {
                prezzo += 20.0;
            } else if (colore.contains("Gold")) {
                prezzo += 50.0;
            }
        }
        return prezzo;
    }

    // --- METODI PER IL DATABASE (La GUI chiama questi, non il DAO) ---

    public Scarpa recuperaScarpaPerId(int id) {
        return scarpaDAO.getScarpaById(id);
    }

    public List<Scarpa> recuperaTutteLeScarpe(String brand) {
        // Qui potresti aggiungere logica extra se servisse
        try {
            return scarpaDAO.cercaScarpe(brand);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Scarpa> ricercaGlobale(String testo) {
        try {
            return scarpaDAO.cercaPerNome(testo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Recensione> getRecensioni(int idScarpa) {
        return scarpaDAO.getRecensioniPerScarpa(idScarpa);
    }

    public double getMediaVoti(int idScarpa) {
        return scarpaDAO.getMediaVoti(idScarpa);
    }

    public boolean aggiungiVoto(int idScarpa, String username, int voto) {
        if (!scarpaDAO.utenteHaGiaVotato(idScarpa, username)) {
            scarpaDAO.aggiungiVoto(idScarpa, username, voto);
            return true;
        }
        return false;
    }
}