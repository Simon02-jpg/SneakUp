package com.sneakup.model.dao;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import java.util.List;

public interface ScarpaDAO {
    void addScarpa(Scarpa scarpa) throws SneakUpException;
    List<Scarpa> getAllScarpe() throws SneakUpException;
    void deleteScarpa(int id) throws SneakUpException;
    void updateScarpa(Scarpa scarpa) throws SneakUpException;
    void aggiungiRecensione(int idScarpa, Recensione r) throws SneakUpException;
    List<Recensione> getRecensioniPerScarpa(int idScarpa) throws SneakUpException;
}