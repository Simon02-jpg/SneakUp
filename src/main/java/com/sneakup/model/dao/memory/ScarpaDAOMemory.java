package com.sneakup.model.dao.memory;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Scarpa;
import java.util.ArrayList;
import java.util.List;

public class ScarpaDAOMemory implements ScarpaDAO {

    private static final List<Scarpa> DB_RAM = new ArrayList<>();
    private static int counterId = 1;

    @Override
    public void addScarpa(Scarpa scarpa) {
        if (scarpa.getId() == 0) {
            scarpa.setId(counterId++);
        }
        DB_RAM.add(scarpa);
    }

    @Override
    public List<Scarpa> getAllScarpe() {
        return new ArrayList<>(DB_RAM);
    }

    // --- NUOVI METODI RICHIESTI DALL'INTERFACCIA ---

    @Override
    public void deleteScarpa(int id) throws SneakUpException {
        // Rimuove l'elemento dalla lista se l'ID corrisponde
        boolean rimosso = DB_RAM.removeIf(s -> s.getId() == id);

        if (!rimosso) {
            throw new SneakUpException("Impossibile eliminare: ID " + id + " non trovato in memoria.");
        }
    }

    @Override
    public void updateScarpa(Scarpa scarpaAggiornata) throws SneakUpException {
        boolean trovato = false;
        // Cerca la scarpa con lo stesso ID e sostituiscila
        for (int i = 0; i < DB_RAM.size(); i++) {
            if (DB_RAM.get(i).getId() == scarpaAggiornata.getId()) {
                DB_RAM.set(i, scarpaAggiornata);
                trovato = true;
                break;
            }
        }

        if (!trovato) {
            throw new SneakUpException("Impossibile aggiornare: ID " + scarpaAggiornata.getId() + " non trovato in memoria.");
        }
    }
}