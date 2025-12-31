package com.sneakup.model.domain;

public class Utente {
    private String username;
    private String email;
    private String password;
    private String indirizzo;
    private String citta;
    private String cap;
    private String numeroCarta;
    private String scadenzaCarta;
    private String cvv;

    public Utente(String username, String email, String password, String indirizzo, String citta, String cap, String numeroCarta, String scadenzaCarta, String cvv) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.cap = cap;
        this.numeroCarta = numeroCarta;
        this.scadenzaCarta = scadenzaCarta;
        this.cvv = cvv;
    }

    // --- GETTERS ---
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getIndirizzo() { return indirizzo; }
    public String getCitta() { return citta; }
    public String getCap() { return cap; }
    public String getNumeroCarta() { return numeroCarta; }
    public String getScadenzaCarta() { return scadenzaCarta; }
    public String getCvv() { return cvv; }

    // --- SETTERS (Aggiunti per risolvere l'errore) ---
    public void setPassword(String password) { this.password = password; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public void setCitta(String citta) { this.citta = citta; }
    public void setCap(String cap) { this.cap = cap; }
    public void setNumeroCarta(String numeroCarta) { this.numeroCarta = numeroCarta; }
    public void setScadenzaCarta(String scadenzaCarta) { this.scadenzaCarta = scadenzaCarta; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
}