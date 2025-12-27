package com.sneakup.model.dao.memory;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ScarpaDAOMemory implements ScarpaDAO {

    private static final List<Scarpa> DB_RAM = new ArrayList<>();
    private static final AtomicInteger counterId = new AtomicInteger(1);

    @Override
    public void addScarpa(Scarpa scarpa) {
        if (scarpa.getId() == 0) {
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
            throw new SneakUpException("ID " + id + " non trovato.");
        }
    }

    @Override
    public void updateScarpa(Scarpa scarpaAggiornata) throws SneakUpException {
        for (int i = 0; i < DB_RAM.size(); i++) {
            if (DB_RAM.get(i).getId() == scarpaAggiornata.getId()) {
                DB_RAM.set(i, scarpaAggiornata);
                return;
            }
        }
        throw new SneakUpException("ID " + scarpaAggiornata.getId() + " non trovato.");
    }

    @Override
    public void aggiungiRecensione(int idScarpa, Recensione r) throws SneakUpException {
        for (Scarpa s : DB_RAM) {
            if (s.getId() == idScarpa) {
                s.getRecensioni().add(r);
                return;
            }
        }
    }

    @Override
    public List<Recensione> getRecensioniPerScarpa(int idScarpa) throws SneakUpException {
        for (Scarpa s : DB_RAM) {
            if (s.getId() == idScarpa) {
                return s.getRecensioni();
            }
        }
        return new ArrayList<>();
    }
}