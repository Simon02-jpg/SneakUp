package com.sneakup.model.dao.memory;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Scarpa;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; // Import necessario

public class ScarpaDAOMemory implements ScarpaDAO {

    private static final List<Scarpa> DB_RAM = new ArrayList<>();
    // Sostituisci int con AtomicInteger per thread-safety e per evitare il warning
    private static final AtomicInteger counterId = new AtomicInteger(1);

    @Override
    public void addScarpa(Scarpa scarpa) {
        if (scarpa.getId() == 0) {
            // Utilizza getAndIncrement() invece di ++
            scarpa.setId(counterId.getAndIncrement());
        }
        DB_RAM.add(scarpa);
    }

    @Override
    public List<Scarpa> getAllScarpe() {
        return new ArrayList<>(DB_RAM);
    }

    @Override
    public void deleteScarpa(int id) throws SneakUpException {
        boolean rimosso = DB_RAM.removeIf(s -> s.getId() == id);
        if (!rimosso) {
            throw new SneakUpException("Impossibile eliminare: ID " + id + " non trovato in memoria.");
        }
    }

    @Override
    public void updateScarpa(Scarpa scarpaAggiornata) throws SneakUpException {
        boolean trovato = false;
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