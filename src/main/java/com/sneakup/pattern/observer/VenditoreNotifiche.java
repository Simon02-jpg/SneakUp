package com.sneakup.pattern.observer;

import com.sneakup.model.domain.Ordine;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VenditoreNotifiche implements Observer {

    private static final Logger logger = Logger.getLogger(VenditoreNotifiche.class.getName());
    private String nomeVenditore;

    public VenditoreNotifiche(String nome) {
        this.nomeVenditore = nome;
    }

    @Override
    public void update(Ordine ordine) {
        // Messaggio informativo principale utilizzando i parametri per l'efficienza
        logger.log(Level.INFO, "NOTIFICA a {0}: Nuovo ordine ricevuto! {1}",
                new Object[]{nomeVenditore, ordine});

        // Sostituzione di System.out con un logger dedicato alla simulazione dell'invio email
        logger.log(Level.INFO, ">> [Email a {0}] Hai venduto delle scarpe! Dettagli ordine ID: {1}",
                new Object[]{nomeVenditore, ordine.getId()});
    }
}