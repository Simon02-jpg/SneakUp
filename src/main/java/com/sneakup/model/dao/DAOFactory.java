package com.sneakup.model.dao;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.fs.ScarpaDAOFileSystem;
import com.sneakup.model.dao.memory.ScarpaDAOMemory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOFactory {

    private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());
    private static DAOFactory instance = null;
    private ScarpaDAO daoInstance = null; // Cache del DAO per non ricrearlo ogni volta

    private DAOFactory() {}

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public ScarpaDAO getScarpaDAO() throws SneakUpException {
        if (daoInstance == null) {
            String type = leggiConfigurazione();

            if ("FILESYSTEM".equalsIgnoreCase(type)) {
                daoInstance = new ScarpaDAOFileSystem();
                System.out.println("LOG: Avvio in modalità FULL (File System)");
            } else {
                daoInstance = new ScarpaDAOMemory();
                System.out.println("LOG: Avvio in modalità DEMO (RAM)");
            }
        }
        return daoInstance;
    }

    private String leggiConfigurazione() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Attenzione: config.properties non trovato, uso default MEMORY");
                return "MEMORY";
            }
            prop.load(input);
            return prop.getProperty("persistence.type", "MEMORY"); // Default MEMORY se manca la chiave
        } catch (IOException ex) {
        // ex.printStackTrace();  <-- RIMUOVI QUESTA RIGA

        // AGGIUNGI QUESTA: Registra l'errore in modo sicuro
        logger.log(Level.SEVERE, "Impossibile leggere il file di configurazione, uso MEMORY come default", ex);

        return "MEMORY";
        }
    }

    // Metodo per i test (per forzare la modalità senza file)
    public void setDemoMode(boolean isDemo) {
        if (isDemo) {
            this.daoInstance = new ScarpaDAOMemory();
        } else {
            this.daoInstance = new ScarpaDAOFileSystem();
        }
    }
}