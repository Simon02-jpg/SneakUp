package com.sneakup.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Carrello {

    private static Carrello instance;
    private List<Scarpa> scarpeSelezionate;
    private double totale;

    private Carrello() {
        this.scarpeSelezionate = new ArrayList<>();
        this.totale = 0.0;
    }

    public static synchronized Carrello getInstance() {
        if (instance == null) {
            instance = new Carrello();
        }
        return instance;
    }

    public void aggiungiScarpa(Scarpa s) {
        scarpeSelezionate.add(s);
        totale += s.getPrezzo();
    }

    public void rimuoviScarpa(Scarpa s) {
        if(scarpeSelezionate.remove(s)) {
            totale -= s.getPrezzo();
        }
    }

    public void svuotaCarrello() {
        scarpeSelezionate.clear();
        totale = 0.0;
    }

    public List<Scarpa> getScarpeSelezionate() {
        return new ArrayList<>(scarpeSelezionate);
    }

    public double getTotale() {
        return totale;
    }
}