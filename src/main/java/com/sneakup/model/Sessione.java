package com.sneakup.model;

public class Sessione {

    private static Sessione instance;
    private String username;
    private String ruolo; // Gestisce "CLIENTE" o "ADMIN"

    private Sessione() {}

    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    /**
     * Registra i dati dell'utente loggato nella sessione.
     */
    public void login(String username, String ruolo) {
        this.username = username;
        this.ruolo = ruolo;
    }

    /**
     * Pulisce i dati della sessione.
     */
    public void logout() {
        this.username = null;
        this.ruolo = null;
    }

    // --- GETTERS ---

    public String getUsername() {
        return username;
    }

    public String getRuolo() {
        return ruolo;
    }

    /**
     * Verifica se un utente è attualmente autenticato.
     */
    public boolean isLoggato() {
        return username != null;
    }

    /**
     * Verifica se l'utente loggato è un amministratore.
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.ruolo);
    }
}