package com.sneakup.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Carrello {

    private List<Scarpa> scarpeSelezionate;
    private double totale;

    public Carrello() {
        this.scarpeSelezionate = new ArrayList<>();
        this.totale = 0.0;
    }

    public void aggiungiScarpa(Scarpa s) {
        this.scarpeSelezionate.add(s);
        this.totale += s.getPrezzo();
    }

    // --- NUOVO METODO AGGIUNTO PER FAR PASSARE IL TEST ---
    public void rimuoviScarpa(Scarpa s) {
        if (this.scarpeSelezionate.remove(s)) {
            this.totale -= s.getPrezzo();

            // Per sicurezza, evitiamo totali negativi dovuti ad arrotondamenti double
            if (this.totale < 0) this.totale = 0.0;
        }
    }

    public void svuotaCarrello() {
        this.scarpeSelezionate.clear();
        this.totale = 0.0;
    }

    public List<Scarpa> getScarpeSelezionate() {
        return scarpeSelezionate;
    }

    public double getTotale() {
        return totale;
    }
}