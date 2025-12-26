package com.sneakup.controller;

import com.sneakup.exception.DatiNonValidiException;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.DAOFactory;
import com.sneakup.model.domain.Scarpa;

public class InserisciScarpaController {

    /**
     * Prende i dati grezzi (Stringhe) dalla View, li converte e salva la scarpa.
     */
    public void inserisciNuovaScarpa(String modello, String marca, String categoria,
                                     String prezzoStr, String tagliaStr, String quantitaStr) throws SneakUpException {

        // 1. Validazione Formato Numerico
        double prezzo;
        double taglia;
        int quantita;

        try {
            prezzo = Double.parseDouble(prezzoStr);
            taglia = Double.parseDouble(tagliaStr);
            quantita = Integer.parseInt(quantitaStr);
        } catch (NumberFormatException e) {
            throw new DatiNonValidiException("Prezzo, Taglia e Quantità devono essere numeri validi.");
        }

        // 2. Validazione Logica (Regole di Business)
        if (prezzo <= 0) {
            throw new DatiNonValidiException("Il prezzo deve essere maggiore di zero.");
        }
        if (taglia < 15 || taglia > 60) {
            throw new DatiNonValidiException("La taglia deve essere compresa tra 15 e 60.");
        }
        if (quantita < 0) {
            throw new DatiNonValidiException("La quantità non può essere negativa.");
        }
        if (modello == null || modello.trim().isEmpty()) {
            throw new DatiNonValidiException("Il campo Modello è obbligatorio.");
        }

        // 3. Creazione Entità
        Scarpa nuovaScarpa = new Scarpa(modello, marca, categoria, taglia, prezzo, quantita);

        // 4. Salvataggio tramite DAO (Indipendente se siamo in Demo o Full)
        DAOFactory.getInstance().getScarpaDAO().addScarpa(nuovaScarpa);
    }

    /**
     * Aggiorna una scarpa esistente mantenendo il suo ID
     */
    public void aggiornaScarpa(int id, String modello, String marca, String categoria,
                               String prezzoStr, String tagliaStr, String quantitaStr) throws SneakUpException {

        // 1. Validazione (Copia identica a quella dell'inserimento)
        double prezzo;
        double taglia;
        int quantita;

        try {
            prezzo = Double.parseDouble(prezzoStr);
            taglia = Double.parseDouble(tagliaStr);
            quantita = Integer.parseInt(quantitaStr);
        } catch (NumberFormatException e) {
            throw new DatiNonValidiException("Prezzo, Taglia e Quantità devono essere numeri validi.");
        }

        if (prezzo <= 0) throw new DatiNonValidiException("Il prezzo deve essere maggiore di zero.");
        if (taglia < 15 || taglia > 60) throw new DatiNonValidiException("Taglia non valida.");
        if (quantita < 0) throw new DatiNonValidiException("Quantità non valida.");
        if (modello == null || modello.trim().isEmpty()) throw new DatiNonValidiException("Modello obbligatorio.");

        // 2. Creazione Oggetto con ID ESISTENTE
        Scarpa scarpaAggiornata = new Scarpa(modello, marca, categoria, taglia, prezzo, quantita);
        scarpaAggiornata.setId(id); // IMPORTANTE: Manteniamo l'ID vecchio!

        // 3. Chiamata al DAO per l'update
        DAOFactory.getInstance().getScarpaDAO().updateScarpa(scarpaAggiornata);
    }
}