package com.sneakup.model;

public class Sessione {

    private static Sessione instance;
    private String username;
    private String ruolo; // Es. "CLIENTE", "ADMIN"

    private Sessione() {}

    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    public void login(String username, String ruolo) {
        this.username = username;
        this.ruolo = ruolo;
    }

    public void logout() {
        this.username = null;
        this.ruolo = null;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggato() {
        return username != null;
    }
}