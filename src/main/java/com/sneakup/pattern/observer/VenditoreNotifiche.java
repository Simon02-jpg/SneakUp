package com.sneakup.pattern.observer;

import com.sneakup.model.domain.Ordine;
import java.util.logging.Logger;

public class VenditoreNotifiche implements Observer {

    private static final Logger logger = Logger.getLogger(VenditoreNotifiche.class.getName());
    private String nomeVenditore;

    public VenditoreNotifiche(String nome) {
        this.nomeVenditore = nome;
    }

    @Override
    public void update(Ordine ordine) {
        // Qui simuli l'invio della mail o notifica push
        logger.info("NOTIFICA a " + nomeVenditore + ": Nuovo ordine ricevuto! " + ordine.toString());
        System.out.println(">> [Email a " + nomeVenditore + "] Hai venduto delle scarpe! Dettagli ordine ID: " + ordine.getId());
    }
}