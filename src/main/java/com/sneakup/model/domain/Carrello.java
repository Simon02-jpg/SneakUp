package com.sneakup.model.domain;

import java.util.ArrayList;
import java.util.List;

public class Carrello {

    // Rimosso: private static Carrello instance;
    private List<Scarpa> scarpeSelezionate;
    private double totale;

    // Costruttore pubblico
    public Carrello() {
        this.scarpeSelezionate = new ArrayList<>();
        this.totale = 0.0;
    }

    // Rimosso: public static synchronized Carrello getInstance() ...

    public void aggiungiScarpa(Scarpa s) {
        this.scarpeSelezionate.add(s);
        this.totale += s.getPrezzo(); // Assumendo che Scarpa abbia getPrezzo()
    }

    // Getter e Setter necessari...
    public List<Scarpa> getScarpeSelezionate() {
        return scarpeSelezionate;
    }

    public double getTotale() {
        return totale;
    }

    // In Carrello.java
    public void svuotaCarrello() {
        this.scarpeSelezionate.clear();
        this.totale = 0.0;
    }
}