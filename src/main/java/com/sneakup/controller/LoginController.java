package com.sneakup.controller;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Ruolo;

public class LoginController {

    public Ruolo login(String username, String password) throws SneakUpException {
        // SIMULAZIONE LOGIN (In seguito potremo collegarlo al DAO Utenti)

        if ("admin".equals(username) && "admin".equals(password)) {
            return Ruolo.VENDITORE;
        }
        else if ("user".equals(username) && "user".equals(password)) {
            return Ruolo.CLIENTE;
        }
        else {
            throw new SneakUpException("Credenziali non valide. Riprova.");
        }
    }
}