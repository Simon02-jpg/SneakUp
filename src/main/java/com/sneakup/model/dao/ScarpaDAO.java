package com.sneakup.model.dao;

import com.sneakup.model.domain.Scarpa;
import com.sneakup.exception.SneakUpException;
import java.util.List;

public interface ScarpaDAO {
    void addScarpa(Scarpa scarpa) throws SneakUpException;
    List<Scarpa> getAllScarpe() throws SneakUpException;

    // NUOVI METODI
    void deleteScarpa(int id) throws SneakUpException;
    void updateScarpa(Scarpa scarpaAggiornata) throws SneakUpException;
}