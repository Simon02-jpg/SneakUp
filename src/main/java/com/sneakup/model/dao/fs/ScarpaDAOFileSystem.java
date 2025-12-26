package com.sneakup.model.dao.fs;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Scarpa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScarpaDAOFileSystem implements ScarpaDAO {

    private static final String FILE_NAME = "scarpe.csv";
    private static final String SEPARATOR = ";";

    @Override
    public void addScarpa(Scarpa scarpa) throws SneakUpException {
        // ... (Codice esistente per addScarpa, lascia invariato) ...
        // Assicurati che il codice esistente usi true nel FileWriter per l'append
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            if (scarpa.getId() == 0) scarpa.setId((int) (System.currentTimeMillis() / 1000));
            scriviRiga(writer, scarpa);
        } catch (IOException e) {
            throw new SneakUpException("Errore scrittura", e);
        }
    }

    @Override
    public List<Scarpa> getAllScarpe() throws SneakUpException {
        // ... (Codice esistente per getAllScarpe, lascia invariato) ...
        // Copia qui la logica di lettura che avevi già fatto
        List<Scarpa> lista = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dati = line.split(SEPARATOR);
                if (dati.length >= 7) {
                    Scarpa s = new Scarpa();
                    s.setId(Integer.parseInt(dati[0]));
                    s.setModello(dati[1]);
                    s.setMarca(dati[2]);
                    s.setCategoria(dati[3]);
                    s.setTaglia(Double.parseDouble(dati[4]));
                    s.setPrezzo(Double.parseDouble(dati[5]));
                    s.setQuantitaDisponibile(Integer.parseInt(dati[6]));
                    lista.add(s);
                }
            }
        } catch (IOException e) {
            throw new SneakUpException("Errore lettura", e);
        }
        return lista;
    }

    // --- NUOVI METODI ---

    @Override
    public void deleteScarpa(int idDaEliminare) throws SneakUpException {
        List<Scarpa> tutte = getAllScarpe();
        // Rimuove se l'ID corrisponde
        boolean rimosso = tutte.removeIf(s -> s.getId() == idDaEliminare);

        if (rimosso) {
            sovrascriviFile(tutte);
        } else {
            throw new SneakUpException("Impossibile eliminare: ID non trovato.");
        }
    }

    @Override
    public void updateScarpa(Scarpa scarpaAggiornata) throws SneakUpException {
        List<Scarpa> tutte = getAllScarpe();
        boolean trovato = false;

        for (int i = 0; i < tutte.size(); i++) {
            if (tutte.get(i).getId() == scarpaAggiornata.getId()) {
                tutte.set(i, scarpaAggiornata); // Sostituisci
                trovato = true;
                break;
            }
        }

        if (trovato) {
            sovrascriviFile(tutte);
        } else {
            throw new SneakUpException("Impossibile modificare: Scarpa non trovata.");
        }
    }

    // Metodo helper per riscrivere tutto il file
    private void sovrascriviFile(List<Scarpa> listaScarpe) throws SneakUpException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) { // FALSE = Sovrascrivi
            for (Scarpa s : listaScarpe) {
                scriviRiga(writer, s);
            }
        } catch (IOException e) {
            throw new SneakUpException("Errore aggiornamento file", e);
        }
    }

    private void scriviRiga(BufferedWriter writer, Scarpa scarpa) throws IOException {
        String riga = scarpa.getId() + SEPARATOR +
                scarpa.getModello() + SEPARATOR +
                scarpa.getMarca() + SEPARATOR +
                scarpa.getCategoria() + SEPARATOR +
                scarpa.getTaglia() + SEPARATOR +
                scarpa.getPrezzo() + SEPARATOR +
                scarpa.getQuantitaDisponibile();
        writer.write(riga);
        writer.newLine();
    }
}