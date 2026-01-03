package com.sneakup.controller;

import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;

public class GestoreUtenti {

    private final UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();

    public Utente login(String email, String password) {
        // Qui potresti aggiungere logica (es. hash password)
        // Per ora deleghi al DAO
        // Nota: Assicurati che UtenteDAOJDBC abbia un metodo per il login che restituisca l'Utente
        // Se restituisce boolean, adatta di conseguenza.
        return utenteDAO.login(email, password);
    }

    public boolean registraUtente(Utente nuovoUtente) {
        // Validazione dati (Business Logic)
        if (nuovoUtente.getEmail() == null || !nuovoUtente.getEmail().contains("@")) {
            return false;
        }
        if (nuovoUtente.getPassword().length() < 4) {
            return false;
        }

        return utenteDAO.registra(nuovoUtente);
    }
}