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

    // 1. AGGIUNTO COSTRUTTORE VUOTO (Indispensabile per il DAO)
    public Ordine() {
        this.scarpeAcquistate = new ArrayList<>();
        this.dataOrdine = LocalDate.now();
    }

    // Il tuo costruttore originale (lo teniamo per quando crei un nuovo ordine da codice)
    public Ordine(int id, List<Scarpa> scarpe, double totale, String indirizzo) {
        this.id = id;
        this.dataOrdine = LocalDate.now();
        this.scarpeAcquistate = new ArrayList<>(scarpe);
        this.totalePagato = totale;
        this.indirizzoSpedizione = indirizzo;
        this.stato = "IN_ELABORAZIONE";
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataOrdine() { return dataOrdine; }
    // 2. AGGIUNTO SETTER PER DATA (Per caricarla dal DB)
    public void setDataOrdine(LocalDate dataOrdine) { this.dataOrdine = dataOrdine; }

    public List<Scarpa> getScarpeAcquistate() { return scarpeAcquistate; }
    // 3. AGGIUNTO SETTER PER LISTA SCARPE
    public void setScarpeAcquistate(List<Scarpa> scarpeAcquistate) { this.scarpeAcquistate = scarpeAcquistate; }

    public double getTotalePagato() { return totalePagato; }
    // 4. AGGIUNTO SETTER PER TOTALE (Risolve l'errore nel DAO)
    public void setTotalePagato(double totalePagato) { this.totalePagato = totalePagato; }

    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public void setIndirizzoSpedizione(String indirizzoSpedizione) { this.indirizzoSpedizione = indirizzoSpedizione; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    @Override
    public String toString() {
        return "Ordine #" + id + " (" + dataOrdine + ") - Totale: €" + String.format("%.2f", totalePagato);
    }
}