package com.sneakup.model.domain;

import java.io.Serializable;
import java.util.Objects;

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

    // Costruttore vuoto (utile per il DAO)
    public Scarpa() {}

    // Costruttore completo
    public Scarpa(String modello, String marca, String categoria, double taglia, double prezzo, int quantita) {
        this.modello = modello;
        this.marca = marca;
        this.categoria = categoria;
        this.taglia = taglia;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantita;
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