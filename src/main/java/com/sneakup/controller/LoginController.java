package com.sneakup.controller;

import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import com.sneakup.model.domain.Ruolo;
import com.sneakup.exception.SneakUpException;

public class LoginController {

    private final UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();

    // MODIFICA: Restituisce l'oggetto Utente intero (o null se fallisce)
    // Questo serve alla GUI per sapere CHI si è loggato (Cliente o Venditore)
    public Utente login(String identificativo, String password) {
        try {
            Utente u = utenteDAO.recuperaDatiUtente(identificativo);

            // Verifica se l'utente esiste e se la password coincide
            if (u != null && u.getPassword().equals(password)) {
                return u; // Login successo: torno l'utente
            }
        } catch (SneakUpException e) {
            e.printStackTrace();
        }
        return null; // Login fallito
    }

    public boolean registraUtente(String username, String password, String email, Ruolo ruolo) {
        try {
            // Creo l'utente con tutti i campi (gli altri vuoti)
            Utente nuovo = new Utente(username, email, password, ruolo, "", "", "", "", "", "");
            return utenteDAO.registra(nuovo);
        } catch (Exception e) {
            System.err.println("Errore in registrazione: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String identificativo, String nuovaPassword) {
        try {
            Utente u = utenteDAO.recuperaDatiUtente(identificativo);
            if (u != null) {
                u.setPassword(nuovaPassword);
                utenteDAO.aggiornaUtente(u);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}