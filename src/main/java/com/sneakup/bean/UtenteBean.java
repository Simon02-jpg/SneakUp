package com.sneakup.bean;

public class UtenteBean {

    private String username;
    private String password;
    private String email;
    private String ruolo; // Es. "CLIENTE", "VENDITORE"

    // Costruttore
    public UtenteBean() {}

    // Getters e Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}