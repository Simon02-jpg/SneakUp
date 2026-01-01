package com.sneakup.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Scarpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String modello;
    private String marca;
    private String categoria;
    private String genere; // Campo Genere
    private double taglia;
    private double prezzo;
    private int quantitaDisponibile;
    private String descrizione;
    private String urlImmagine;

    // Campo temporaneo per la demo (se non ci sono recensioni vere)
    private int mockVoto = 0;

    private List<Recensione> recensioni;

    // --- COSTRUTTORE VUOTO ---
    public Scarpa() {
        this.recensioni = new ArrayList<>();
    }

    // --- COSTRUTTORE COMPLETO ---
    public Scarpa(String modello, String marca, String categoria, double taglia, double prezzo, int quantita) {
        this.modello = modello;
        this.marca = marca;
        this.categoria = categoria;
        this.taglia = taglia;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantita;
        this.recensioni = new ArrayList<>();
    }

    // --- METODO CHE MANCAVA (Calcola la media reale) ---
    public double getMediaVoti() {
        if (recensioni == null || recensioni.isEmpty()) return 0.0;
        double somma = 0;
        for (Recensione r : recensioni) {
            somma += r.getVoto();
        }
        return somma / recensioni.size();
    }

    // --- GETTERS & SETTERS ---
    public int getMockVoto() { return mockVoto; }
    public void setMockVoto(int mockVoto) { this.mockVoto = mockVoto; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

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
    public void setQuantitaDisponibile(int quantita) { this.quantitaDisponibile = quantita; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getUrlImmagine() { return urlImmagine; }
    public void setUrlImmagine(String url) { this.urlImmagine = url; }

    public List<Recensione> getRecensioni() { return recensioni; }
    public void aggiungiRecensione(Recensione r) {
        if(this.recensioni == null) this.recensioni = new ArrayList<>();
        this.recensioni.add(r);
    }

    @Override
    public String toString() { return marca + " " + modello; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scarpa scarpa = (Scarpa) o;
        return id == scarpa.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}