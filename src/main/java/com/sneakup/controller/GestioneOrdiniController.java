package com.sneakup.controller;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.DAOFactory;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Carrello;
import com.sneakup.model.domain.Ordine;
import com.sneakup.model.domain.Scarpa;
import com.sneakup.pattern.observer.Observer;
import com.sneakup.pattern.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class GestioneOrdiniController implements Subject {

    private List<Observer> observers = new ArrayList<>();
    private ScarpaDAO scarpaDAO;

    public GestioneOrdiniController() {
        this.scarpaDAO = DAOFactory.getInstance().getScarpaDAO();
    }

    /**
     * Metodo principale per processare l'ordine.
     * Verifica disponibilità, salva ordine (se avessimo OrdineDAO) e aggiorna quantità.
     */
    public void effettuaOrdine(Carrello carrello, String indirizzoSpedizione) throws SneakUpException {
        if (carrello == null || carrello.getScarpeSelezionate().isEmpty()) {
            throw new SneakUpException("Il carrello è vuoto.");
        }

        // 1. Verifica disponibilità e aggiorna magazzino
        // Nota: in un caso reale questo dovrebbe essere transazionale
        for (Scarpa s : carrello.getScarpeSelezionate()) {
            // Ricarichiamo la scarpa dal DAO per avere la quantità fresca
            // (Assumendo che getAllScarpe o un metodo getById esista)
            // Per semplicità qui decrementiamo direttamente l'oggetto passato,
            // ma dovremmo usare updateScarpa del DAO.

            int nuovaQuantita = s.getQuantitaDisponibile() - 1;
            if (nuovaQuantita < 0) {
                throw new SneakUpException("Prodotto non più disponibile: " + s.getModello());
            }
            s.setQuantitaDisponibile(nuovaQuantita);
            scarpaDAO.updateScarpa(s);
        }

        // 2. Crea l'oggetto Ordine
        int fakeOrderId = (int) (System.currentTimeMillis() / 1000);
        Ordine nuovoOrdine = new Ordine(fakeOrderId, carrello.getScarpeSelezionate(), carrello.getTotale(), indirizzoSpedizione);

        // 3. (Opzionale) Salva Ordine su DB tramite OrdineDAO (se implementato)
        // OrdineDAO.getInstance().save(nuovoOrdine);

        // 4. Notifica gli Observer (Requirement 3)
        notifyObservers(nuovoOrdine);
    }

    // --- Implementazione Pattern Observer ---

    @Override
    public void attach(Observer o) {
        observers.add(o);
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
}