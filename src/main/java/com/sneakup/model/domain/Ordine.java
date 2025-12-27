package com.sneakup.model.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ordine {
    private int id;
    private LocalDate dataOrdine;
    private List<Scarpa> scarpeAcquistate;
    private double totalePagato;
    private String indirizzoSpedizione;
    private String stato; // Es. "IN_ELABORAZIONE", "SPEDITO"

    public Ordine(int id, List<Scarpa> scarpe, double totale, String indirizzo) {
        this.id = id;
        this.dataOrdine = LocalDate.now();
        // Creiamo una copia della lista per sicurezza
        this.scarpeAcquistate = new ArrayList<>(scarpe);
        this.totalePagato = totale;
        this.indirizzoSpedizione = indirizzo;
        this.stato = "IN_ELABORAZIONE";
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataOrdine() { return dataOrdine; }

    public List<Scarpa> getScarpeAcquistate() { return scarpeAcquistate; }

    public double getTotalePagato() { return totalePagato; }

    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public void setIndirizzoSpedizione(String indirizzoSpedizione) { this.indirizzoSpedizione = indirizzoSpedizione; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    @Override
    public String toString() {
        return "Ordine #" + id + " (" + dataOrdine + ") - Totale: €" + String.format("%.2f", totalePagato);
    }
}