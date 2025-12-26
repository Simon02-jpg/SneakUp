package com.sneakup.model.dao;

import com.sneakup.model.dao.fs.ScarpaDAOFileSystem;
import com.sneakup.model.dao.memory.ScarpaDAOMemory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOFactory {

    private static final Logger logger = Logger.getLogger(DAOFactory.class.getName());
    // 1. DEFINITA COSTANTE PER EVITARE DUPLICAZIONI
    private static final String DEFAULT_MODE = "MEMORY";

    private static DAOFactory instance = null;
    private ScarpaDAO daoInstance = null; // Cache del DAO per non ricrearlo ogni volta

    private DAOFactory() {}

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public ScarpaDAO getScarpaDAO() {
        if (daoInstance == null) {
            String type = leggiConfigurazione();

            if ("FILESYSTEM".equalsIgnoreCase(type)) {
                daoInstance = new ScarpaDAOFileSystem();
                // 2. USO LOGGER INVECE DI SYSTEM.OUT
                logger.info("LOG: Avvio in modalità FULL (File System)");
            } else {
                daoInstance = new ScarpaDAOMemory();
                // 2. USO LOGGER INVECE DI SYSTEM.OUT
                logger.info("LOG: Avvio in modalità DEMO (RAM)");
            }
        }
        return daoInstance;
    }

    private String leggiConfigurazione() {
        Properties prop = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                // 2. USO LOGGER (WARNING) INVECE DI SYSTEM.OUT
                logger.warning("Attenzione: config.properties non trovato, uso default MEMORY");
                return DEFAULT_MODE; // 3. USO LA COSTANTE
            }
            prop.load(input);
            return prop.getProperty("persistence.type", DEFAULT_MODE); // 3. USO LA COSTANTE

        } catch (IOException ex) {
            // Log dell'errore corretto
            logger.log(Level.SEVERE, "Impossibile leggere il file di configurazione, uso MEMORY come default", ex);
            return DEFAULT_MODE; // 3. USO LA COSTANTE
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