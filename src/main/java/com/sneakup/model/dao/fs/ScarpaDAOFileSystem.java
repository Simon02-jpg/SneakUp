package com.sneakup.model.dao.fs;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.ScarpaDAO;
import com.sneakup.model.domain.Recensione;
import com.sneakup.model.domain.Scarpa;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScarpaDAOFileSystem implements ScarpaDAO {

    private static final String FILE_NAME = "scarpe.csv";
    private static final String SEPARATOR = ";";

    @Override
    public void addScarpa(Scarpa scarpa) throws SneakUpException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            if (scarpa.getId() == 0) scarpa.setId((int) (System.currentTimeMillis() / 1000));
            scriviRiga(writer, scarpa);
        } catch (IOException e) {
            throw new SneakUpException("Errore scrittura file", e);
        }
    }

    @Override
    public List<Scarpa> getAllScarpe() throws SneakUpException {
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
            throw new SneakUpException("Errore lettura file", e);
        }
        return lista;
    }

    @Override
    public void deleteScarpa(int id) throws SneakUpException {
        List<Scarpa> tutte = getAllScarpe();
        if (tutte.removeIf(s -> s.getId() == id)) {
            sovrascriviFile(tutte);
        } else throw new SneakUpException("ID non trovato.");
    }

    @Override
    public void updateScarpa(Scarpa s) throws SneakUpException {
        List<Scarpa> tutte = getAllScarpe();
        boolean trovato = false;
        for (int i = 0; i < tutte.size(); i++) {
            if (tutte.get(i).getId() == s.getId()) {
                tutte.set(i, s);
                trovato = true;
                break;
            }
        }
        if (trovato) sovrascriviFile(tutte);
        else throw new SneakUpException("ID non trovato.");
    }

    @Override
    public void aggiungiRecensione(int id, Recensione r) { /* Non implementato su FS */ }

    @Override
    public List<Recensione> getRecensioniPerScarpa(int id) { return new ArrayList<>(); }

    private void sovrascriviFile(List<Scarpa> lista) throws SneakUpException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Scarpa s : lista) scriviRiga(writer, s);
        } catch (IOException e) { throw new SneakUpException("Errore sovrascrittura", e); }
    }

    private void scriviRiga(BufferedWriter w, Scarpa s) throws IOException {
        w.write(s.getId()+SEPARATOR+s.getModello()+SEPARATOR+s.getMarca()+SEPARATOR+s.getCategoria()+SEPARATOR+s.getTaglia()+SEPARATOR+s.getPrezzo()+SEPARATOR+s.getQuantitaDisponibile());
        w.newLine();
    }
}