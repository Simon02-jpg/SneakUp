package com.sneakup.controller;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.domain.Ruolo;

import java.util.Map;

public class LoginController {

    // Simuliamo un database in memoria (mappa username -> password)
    private static final Map<String, String> MOCK_DB_VENDITORI = Map.of("admin", "admin");
    private static final Map<String, String> MOCK_DB_CLIENTI = Map.of("user", "user");

    public Ruolo login(String username, String password) throws SneakUpException {

        if (checkCredentials(MOCK_DB_VENDITORI, username, password)) {
            return Ruolo.VENDITORE;
        }
        else if (checkCredentials(MOCK_DB_CLIENTI, username, password)) {
            return Ruolo.CLIENTE;
        }

        throw new SneakUpException("Credenziali non valide");
    }

    // Metodo helper per verificare le credenziali
    private boolean checkCredentials(Map<String, String> db, String user, String pass) {
        return db.containsKey(user) && db.get(user).equals(pass);
    }
}