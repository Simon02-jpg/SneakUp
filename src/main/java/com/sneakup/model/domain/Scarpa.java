package com.sneakup.model.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class Scarpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String modello;
    private String marca;
    private String categoria; // Es. Running, Basket
    private double taglia;
    private double prezzo;
    private int quantitaDisponibile;
    private String descrizione;
    private String urlImmagine;

    // NUOVO CAMPO
    private List<Recensione> recensioni;

    // Aggiorna il costruttore vuoto
    public Scarpa() {
        this.recensioni = new ArrayList<>();
    }

    // Aggiorna il costruttore completo
    public Scarpa(String modello, String marca, String categoria, double taglia, double prezzo, int quantita) {
        this.modello = modello;
        this.marca = marca;
        this.categoria = categoria;
        this.taglia = taglia;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantita;
        // Inizializza la lista
        this.recensioni = new ArrayList<>();
    }

    // Getter e Setter per le recensioni
    public List<Recensione> getRecensioni() {
        return recensioni;
    }

    public void aggiungiRecensione(Recensione r) {
        this.recensioni.add(r);
    }

    // Metodo di comodo per calcolare la media voti
    public double getMediaVoti() {
        if (recensioni.isEmpty()) return 0.0;
        double somma = 0;
        for (Recensione r : recensioni) {
            somma += r.getVoto();
        }
        return somma / recensioni.size();
    }



    // --- GETTERS & SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getTaglia() { return taglia; }
    public void setTaglia(double taglia) { this.taglia = taglia; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public int getQuantitaDisponibile() { return quantitaDisponibile; }
    public void setQuantitaDisponibile(int quantitaDisponibile) { this.quantitaDisponibile = quantitaDisponibile; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getUrlImmagine() { return urlImmagine; }
    public void setUrlImmagine(String urlImmagine) { this.urlImmagine = urlImmagine; }

    @Override
    public String toString() {
        return String.format("%s %s (Taglia: %.1f) - €%.2f", marca, modello, taglia, prezzo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scarpa scarpa = (Scarpa) o;
        return id == scarpa.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}