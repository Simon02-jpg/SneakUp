-- 1. Creazione del database
DROP DATABASE IF EXISTS sneakup_db;
CREATE DATABASE sneakup_db DEFAULT CHARACTER SET = 'utf8mb4';
USE sneakup_db;

-- 2. Tabella LOGIN (per gestire account personali - User Story 3)
DROP TABLE IF EXISTS `LOGIN`;
CREATE TABLE IF NOT EXISTS `LOGIN` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `USERNAME` VARCHAR(45) NOT NULL,
  `PASSWORD` VARCHAR(45) NOT NULL,
  `ROLE` INT NOT NULL DEFAULT 2, -- 0=Admin, 1=Gestore Vendite, 2=Cliente
  PRIMARY KEY (`ID`)
);

-- 3. Tabella SCARPE (Catalogo - Requisito Funzionale 1 e 2)
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

-- 4. Tabella RECENSIONI (User Story 2)
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

-- 5. Tabella ORDINI (Use Case: Effettua Ordine)
DROP TABLE IF EXISTS `ORDINI`;
CREATE TABLE IF NOT EXISTS `ORDINI` (
  `idORDINE` INT NOT NULL AUTO_INCREMENT,
  `idUtente` INT NOT NULL,
  `totale` DOUBLE NOT NULL,
  `indirizzo` VARCHAR(255) NOT NULL,
  `data_ordine` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idORDINE`),
  FOREIGN KEY (`idUtente`) REFERENCES `LOGIN`(`ID`)
);

-- -----------------------------------------------------
-- INSERIMENTO DATI DI ESEMPIO
-- -----------------------------------------------------

-- Utenti
INSERT INTO LOGIN (USERNAME, PASSWORD, ROLE) VALUES ('seller', 'seller', 0);
INSERT INTO LOGIN (USERNAME, PASSWORD, ROLE) VALUES ('client', 'client', 1);

-- Catalogo Scarpe
INSERT INTO SCARPE (modello, marca, categoria, taglia, prezzo, quantita, descrizione)
VALUES ('Air Max 270', 'Nike', 'Lifestyle', 42.5, 150.00, 15, 'Ammortizzazione leggendaria');

INSERT INTO SCARPE (modello, marca, categoria, taglia, prezzo, quantita, descrizione)
VALUES ('Predator', 'Adidas', 'Calcio', 44.0, 220.00, 8, 'Controllo palla assoluto');

-- Recensioni di prova
INSERT INTO RECENSIONI (idSCARPA, autore, voto, testo)
VALUES (1, 'Mario88', 5, 'Comodissime per camminare!');