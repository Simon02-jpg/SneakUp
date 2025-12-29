-- 1. Creazione del database
DROP DATABASE IF EXISTS sneakup_db;
CREATE DATABASE sneakup_db DEFAULT CHARACTER SET = 'utf8mb4';
USE sneakup_db;

-- 2. Tabella UTENTE (Ex LOGIN - Aggiornata con dati personali e carte)
DROP TABLE IF EXISTS `UTENTE`;
CREATE TABLE IF NOT EXISTS `UTENTE` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `USERNAME` VARCHAR(45) NOT NULL,
  `PASSWORD` VARCHAR(45) NOT NULL,
  `ROLE` INT NOT NULL DEFAULT 2, -- 0=Admin, 1=Cliente
  `NOME` VARCHAR(50) DEFAULT NULL,
  `COGNOME` VARCHAR(50) DEFAULT NULL,
  `EMAIL` VARCHAR(100) DEFAULT NULL,

  -- NUOVI CAMPI PER AREA PERSONALE
  `INDIRIZZO` VARCHAR(255) DEFAULT NULL,
  `CITTA` VARCHAR(100) DEFAULT NULL,
  `CAP` VARCHAR(10) DEFAULT NULL,
  `NUMERO_CARTA` VARCHAR(20) DEFAULT NULL,
  `SCADENZA_CARTA` VARCHAR(10) DEFAULT NULL,
  `CVV` VARCHAR(5) DEFAULT NULL,

  PRIMARY KEY (`ID`),
  UNIQUE INDEX `USERNAME_UNIQUE` (`USERNAME` ASC),
  UNIQUE INDEX `EMAIL_UNIQUE` (`EMAIL` ASC)
);

-- 3. Tabella SCARPE (Catalogo)
DROP TABLE IF EXISTS `SCARPE`;
CREATE TABLE IF NOT EXISTS `SCARPE` (
  `idSCARPA` INT NOT NULL AUTO_INCREMENT,
  `modello` VARCHAR(100) NOT NULL,
  `marca` VARCHAR(100) NOT NULL,
  `categoria` VARCHAR(50) NOT NULL,
  `taglia` DOUBLE NOT NULL,
  `prezzo` DOUBLE NOT NULL,
  `quantita` INT NOT NULL,
  `descrizione` TEXT,
  PRIMARY KEY (`idSCARPA`)
);

-- 4. Tabella RECENSIONI
DROP TABLE IF EXISTS `RECENSIONI`;
CREATE TABLE IF NOT EXISTS `RECENSIONI` (
  `idRECENSIONE` INT NOT NULL AUTO_INCREMENT,
  `idSCARPA` INT NOT NULL,
  `autore` VARCHAR(45) NOT NULL,
  `voto` INT NOT NULL, -- Da 1 a 5
  `testo` TEXT,
  PRIMARY KEY (`idRECENSIONE`),
  FOREIGN KEY (`idSCARPA`) REFERENCES `SCARPE`(`idSCARPA`) ON DELETE CASCADE
);

-- 5. Tabella ORDINI
DROP TABLE IF EXISTS `ORDINI`;
CREATE TABLE IF NOT EXISTS `ORDINI` (
  `idORDINE` INT NOT NULL AUTO_INCREMENT,
  `idUtente` INT NOT NULL,
  `totale` DOUBLE NOT NULL,
  `indirizzo` VARCHAR(255) NOT NULL,
  `data_ordine` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idORDINE`),
  FOREIGN KEY (`idUtente`) REFERENCES `UTENTE`(`ID`) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- INSERIMENTO DATI DI ESEMPIO
-- -----------------------------------------------------

-- Admin
INSERT INTO UTENTE (USERNAME, PASSWORD, ROLE, NOME, COGNOME, EMAIL)
VALUES ('seller', 'seller', 0, 'Admin', 'Principale', 'admin@sneakup.com');

-- Cliente (con alcuni dati già compilati per test)
INSERT INTO UTENTE (USERNAME, PASSWORD, ROLE, NOME, COGNOME, EMAIL, INDIRIZZO, CITTA, CAP)
VALUES ('client', 'client', 1, 'Mario', 'Rossi', 'mario.rossi@email.it', 'Via Roma 10', 'Milano', '20100');

-- Catalogo Scarpe
INSERT INTO SCARPE (modello, marca, categoria, taglia, prezzo, quantita, descrizione)
VALUES ('Air Max 270', 'Nike', 'Lifestyle', 42.5, 150.00, 15, 'Ammortizzazione leggendaria');

INSERT INTO SCARPE (modello, marca, categoria, taglia, prezzo, quantita, descrizione)
VALUES ('Predator', 'Adidas', 'Calcio', 44.0, 220.00, 8, 'Controllo palla assoluto');

-- Recensioni di prova
INSERT INTO RECENSIONI (idSCARPA, autore, voto, testo)
VALUES (1, 'Mario88', 5, 'Comodissime per camminare!');