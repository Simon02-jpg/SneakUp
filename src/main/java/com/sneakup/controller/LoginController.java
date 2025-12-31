package com.sneakup.controller;

import com.sneakup.model.dao.db.UtenteDAOJDBC;
import com.sneakup.model.domain.Utente;
import com.sneakup.model.domain.Ruolo;
import com.sneakup.exception.SneakUpException;

public class LoginController {

    private final UtenteDAOJDBC utenteDAO = new UtenteDAOJDBC();

    // Metodo chiamato dalla LoginGUIController
    public boolean login(String identificativo, String password) {
        try {
            Utente u = utenteDAO.recuperaDatiUtente(identificativo);
            return u != null && u.getPassword().equals(password);
        } catch (SneakUpException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo chiamato dalla RegistrazioneGUIController
    public boolean registraUtente(String username, String password, String email, Ruolo ruolo) {
        try {
            // Crea l'oggetto Utente con i dati minimi per il DB
            Utente nuovo = new Utente(username, email, password, "", "", "", "", "", "");
            utenteDAO.salvaNuovoUtente(nuovo);
            return true;
        } catch (Exception e) {
            System.err.println("Errore in registrazione: " + e.getMessage());
            return false;
        }
    }

    // Metodo chiamato dalla RecuperoPasswordGUIController
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