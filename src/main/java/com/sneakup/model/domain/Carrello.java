package com.sneakup.model.domain;

import com.sneakup.pattern.observer.Subject;
import com.sneakup.pattern.observer.Observer;
import java.util.ArrayList;
import java.util.List;

public class Carrello implements Subject {

    private List<Scarpa> scarpeSelezionate;
    private double totale;
    private List<Observer> observers;

    public Carrello() {
        this.scarpeSelezionate = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.totale = 0.0;
    }

    // --- IMPLEMENTAZIONE METODI INTERFACCIA SUBJECT ---
    @Override
    public void attach(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Ordine ordine) {
        for (Observer o : observers) {
            o.update(ordine);
        }
    }

    // --- METODI DEL CARRELLO ---
    public void aggiungiScarpa(Scarpa s) {
        this.scarpeSelezionate.add(s);
        this.totale += s.getPrezzo();
    }

    public void rimuoviScarpa(Scarpa s) {
        if (this.scarpeSelezionate.remove(s)) {
            this.totale -= s.getPrezzo();
            if (this.totale < 0) this.totale = 0.0;
        }
    }

    public List<Scarpa> getScarpe() {
        return new ArrayList<>(this.scarpeSelezionate);
    }

    public double getTotale() {
        double calcoloTotale = 0;
        for (Scarpa s : scarpeSelezionate) {
            calcoloTotale += s.getPrezzo();
        }
        return calcoloTotale;
    }

    public void svuota(Ordine ordineAppenaFatto) {
        this.scarpeSelezionate.clear();
        this.totale = 0.0;
        // Notifica gli osservatori passando l'oggetto Ordine
        notifyObservers(ordineAppenaFatto);
    }

    public List<Scarpa> getScarpeSelezionate() {
        return scarpeSelezionate;
    }
}