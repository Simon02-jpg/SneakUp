package com.sneakup.controller;

import com.sneakup.bean.ScarpaBean; // Assicurati di aver creato questa classe nel package bean
import com.sneakup.exception.DatiNonValidiException;
import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.DAOFactory;
import com.sneakup.model.domain.Scarpa;

public class InserisciScarpaController {

    /**
     * Prende i dati incapsulati nel Bean (Stringhe), li valida, converte e salva.
     */
    public void inserisciNuovaScarpa(ScarpaBean bean) throws SneakUpException {

        // 1. Estrazione dei dati dal Bean (per comodità e leggibilità)
        String modello = bean.getModello();
        String marca = bean.getMarca();
        String categoria = bean.getCategoria();
        String prezzoStr = bean.getPrezzo();
        String tagliaStr = bean.getTaglia();
        String quantitaStr = bean.getQuantita();

        // 2. Validazione Formato Numerico
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

        // 3. Validazione Logica (Regole di Business)
        validazioneLogica(modello, prezzo, taglia, quantita);

        // 4. Creazione Entità
        Scarpa nuovaScarpa = new Scarpa(modello, marca, categoria, taglia, prezzo, quantita);

        // 5. Salvataggio tramite DAO
        DAOFactory.getInstance().getScarpaDAO().addScarpa(nuovaScarpa);
    }

    /**
     * Aggiorna una scarpa esistente usando i dati del Bean e l'ID passato.
     */
    public void aggiornaScarpa(int id, ScarpaBean bean) throws SneakUpException {

        // 1. Estrazione (Identica all'inserimento)
        String modello = bean.getModello();
        String marca = bean.getMarca();
        String categoria = bean.getCategoria();
        String prezzoStr = bean.getPrezzo();
        String tagliaStr = bean.getTaglia();
        String quantitaStr = bean.getQuantita();

        // 2. Conversione
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

        // 3. Validazione Logica
        validazioneLogica(modello, prezzo, taglia, quantita);

        // 4. Creazione Oggetto con ID ESISTENTE
        Scarpa scarpaAggiornata = new Scarpa(modello, marca, categoria, taglia, prezzo, quantita);
        scarpaAggiornata.setId(id); // IMPORTANTE: Manteniamo l'ID vecchio!

        // 5. Chiamata al DAO per l'update
        DAOFactory.getInstance().getScarpaDAO().updateScarpa(scarpaAggiornata);
    }

    /**
     * Metodo privato per evitare duplicazione di codice nella validazione (Aumenta la qualità del codice).
     */
    private void validazioneLogica(String modello, double prezzo, double taglia, int quantita) throws DatiNonValidiException {
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
    }
}