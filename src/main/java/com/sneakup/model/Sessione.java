package com.sneakup.model;

import com.sneakup.model.dao.db.PreferitiDAOJDBC;
import com.sneakup.model.dao.db.CarrelloDAOJDBC;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.model.domain.Utente; // Importante

import java.util.ArrayList;
import java.util.List;

public class Sessione {

    private static Sessione instance = null;

    private String username;
    private String ruolo;
    private boolean loggato;

    // NUOVO: Oggetto Utente completo (per avere email, indirizzo, ecc. nell'area personale)
    private Utente utenteCorrente;

    private List<Scarpa> listaPreferiti;
    private List<Scarpa> carrello;

    private final PreferitiDAOJDBC preferitiDAO;
    private final CarrelloDAOJDBC carrelloDAO;

    private Sessione() {
        this.loggato = false;
        this.listaPreferiti = new ArrayList<>();
        this.carrello = new ArrayList<>();
        this.preferitiDAO = new PreferitiDAOJDBC();
        this.carrelloDAO = new CarrelloDAOJDBC();
    }

    public static synchronized Sessione getInstance() {
        if (instance == null) instance = new Sessione();
        return instance;
    }

    /**
     * Sincronizza i dati locali con il DB al momento del login.
     */
    public void login(String username, String ruolo) {
        // Backup temporaneo del carrello creato da "ospite"
        List<Scarpa> carrelloOspite = new ArrayList<>(this.carrello);

        this.username = username;
        this.ruolo = ruolo;
        this.loggato = true;

        // 1. Carica dati dal DB (Preferiti e Carrello salvati in precedenza)
        this.listaPreferiti = preferitiDAO.caricaPreferiti(username);
        this.carrello = carrelloDAO.caricaCarrello(username);

        // 2. Unisce il carrello dell'ospite con quello dell'utente nel DB
        for (Scarpa s : carrelloOspite) {
            this.carrello.add(s);
            carrelloDAO.salva(this.username, s.getId());
        }

        System.out.println("Sessione attiva per: " + username);
    }

    public void logout() {
        this.username = null;
        this.ruolo = null;
        this.utenteCorrente = null; // Pulisce l'utente completo
        this.loggato = false;
        this.listaPreferiti.clear();
        this.carrello.clear();
    }

    // --- GETTERS & SETTERS (Utente Completo) ---

    public void setUtente(Utente u) {
        this.utenteCorrente = u;
        // Aggiorna anche il campo username per coerenza, se necessario
        if (u != null) {
            this.username = u.getUsername();
        }
    }

    public Utente getUtente() {
        return utenteCorrente;
    }

    // --- GETTERS STANDARD ---
    public boolean isLoggato() { return loggato; }
    public String getUsername() { return username; }
    public String getRuolo() { return ruolo; }
    public List<Scarpa> getPreferiti() { return listaPreferiti; }
    public List<Scarpa> getCarrello() { return carrello; }

    // --- LOGICA PREFERITI ---
    public boolean isPreferito(Scarpa s) {
        if (s == null) return false;
        return listaPreferiti.stream().anyMatch(p -> p.getId() == s.getId());
    }

    public void aggiungiPreferito(Scarpa s) {
        if (loggato && s != null && !isPreferito(s)) {
            listaPreferiti.add(s);
            preferitiDAO.salva(this.username, s.getId());
        }
    }

    public void rimuoviPreferito(Scarpa s) {
        if (loggato && s != null) {
            listaPreferiti.removeIf(p -> p.getId() == s.getId());
            preferitiDAO.rimuovi(this.username, s.getId());
        }
    }

    // --- LOGICA CARRELLO ---
    public void aggiungiAlCarrello(Scarpa s) {
        if (s == null) return;
        carrello.add(s);
        if (loggato) {
            carrelloDAO.salva(this.username, s.getId());
        }
    }

    public void rimuoviDalCarrello(Scarpa s) {
        if (s == null) return;
        // Rimuove la prima occorrenza trovata
        for (int i = 0; i < carrello.size(); i++) {
            if (carrello.get(i).getId() == s.getId()) {
                carrello.remove(i);
                break;
            }
        }
        if (loggato) {
            carrelloDAO.rimuovi(this.username, s.getId());
        }
    }

    public void svuotaCarrello() {
        carrello.clear();
        if (loggato) {
            carrelloDAO.svuota(this.username);
        }
    }
}