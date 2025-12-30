package com.sneakup.bean;

public class ScarpaBean {

    private String modello;
    private String marca;
    private String categoria;
    private String taglia;   // Usiamo String perché dalla GUI arriva testo
    private String prezzo;   // Usiamo String per validarlo dopo nel Controller
    private String quantita; // Idem

    // Costruttore vuoto
    public ScarpaBean() {}

    // Getters e Setters
    public String getModello() { return modello; }
    public void setModello(String modello) { this.modello = modello; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTaglia() { return taglia; }
    public void setTaglia(String taglia) { this.taglia = taglia; }

    public String getPrezzo() { return prezzo; }
    public void setPrezzo(String prezzo) { this.prezzo = prezzo; }

    public String getQuantita() { return quantita; }
    public void setQuantita(String quantita) { this.quantita = quantita; }
}