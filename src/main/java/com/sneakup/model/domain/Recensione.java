package com.sneakup.model.domain;

import java.io.Serializable;

public class Recensione implements Serializable {
    private static final long serialVersionUID = 1L;

    private String autore;
    private int voto; // Valore da 1 a 5
    private String testo;

    public Recensione(String autore, int voto, String testo) {
        this.autore = autore;
        this.voto = voto;
        this.testo = testo;
    }

    public String getAutore() { return autore; }
    public int getVoto() { return voto; }
    public String getTesto() { return testo; }

    @Override
    public String toString() {
        return String.format("%s (%d/5): %s", autore, voto, testo);
    }
}