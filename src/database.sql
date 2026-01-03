-- ==========================================
-- CREAZIONE DATABASE SNEAKUP
-- ==========================================
DROP DATABASE IF EXISTS sneakup_db;
CREATE DATABASE sneakup_db;
USE sneakup_db;

-- ==========================================
-- 1. TABELLA UTENTE
-- ==========================================
DROP TABLE IF EXISTS UTENTE;
CREATE TABLE UTENTE (
    USERNAME VARCHAR(50) NOT NULL PRIMARY KEY,
    EMAIL VARCHAR(100) NOT NULL UNIQUE,
    PASSWORD VARCHAR(50) NOT NULL,
    RUOLO VARCHAR(20) DEFAULT 'CLIENTE',
    INDIRIZZO VARCHAR(100),
    CITTA VARCHAR(50),
    CAP VARCHAR(10),
    NUMERO_CARTA VARCHAR(20),
    SCADENZA_CARTA VARCHAR(10),
    CVV VARCHAR(5)
);

-- ==========================================
-- 2. TABELLA SCARPE
-- ==========================================
DROP TABLE IF EXISTS SCARPE;
CREATE TABLE SCARPE (
    idSCARPA INT AUTO_INCREMENT PRIMARY KEY,
    modello VARCHAR(100) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    categoria VARCHAR(50),
    genere VARCHAR(10) NOT NULL,
    taglia DOUBLE NOT NULL,
    prezzo DOUBLE NOT NULL,
    quantita INT NOT NULL DEFAULT 0,
    descrizione TEXT,
    url_immagine VARCHAR(255)
);

-- ==========================================
-- 3. TABELLA RECENSIONE
-- ==========================================
DROP TABLE IF EXISTS RECENSIONE;
CREATE TABLE RECENSIONE (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_scarpa INT NOT NULL,
    username_utente VARCHAR(50) NOT NULL,
    voto INT NOT NULL CHECK (voto >= 1 AND voto <= 5),
    testo TEXT,
    data_inserimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_scarpa) REFERENCES SCARPE(idSCARPA) ON DELETE CASCADE,
    FOREIGN KEY (username_utente) REFERENCES UTENTE(USERNAME) ON DELETE CASCADE
);

-- ==========================================
-- 4. TABELLA PREFERITI
-- ==========================================
DROP TABLE IF EXISTS PREFERITI;
CREATE TABLE PREFERITI (
    utente VARCHAR(50) NOT NULL,
    id_scarpa INT NOT NULL,
    PRIMARY KEY (utente, id_scarpa),
    FOREIGN KEY (utente) REFERENCES UTENTE(USERNAME) ON DELETE CASCADE,
    FOREIGN KEY (id_scarpa) REFERENCES SCARPE(idSCARPA) ON DELETE CASCADE
);

-- ==========================================
-- 5. TABELLA CARRELLO
-- ==========================================
DROP TABLE IF EXISTS carrello;
CREATE TABLE carrello (
    username VARCHAR(50) NOT NULL,
    id_scarpa INT NOT NULL,
    PRIMARY KEY (username, id_scarpa),
    FOREIGN KEY (username) REFERENCES UTENTE(USERNAME) ON DELETE CASCADE,
    FOREIGN KEY (id_scarpa) REFERENCES SCARPE(idSCARPA) ON DELETE CASCADE
);

-- ==========================================
-- POPOLAMENTO DATI - UTENTI (AGGIORNATO CON RUOLO)
-- ==========================================
INSERT INTO UTENTE (USERNAME, EMAIL, PASSWORD, RUOLO, INDIRIZZO, CITTA, CAP, NUMERO_CARTA, SCADENZA_CARTA, CVV) VALUES
('seller', 'admin@sneakup.com', 'admin', 'VENDITORE', 'Via Centrale 1', 'Roma', '00100', '1111222233334444', '12/30', '999'),
('mario', 'mario@email.com', '1234', 'CLIENTE', 'Via Roma 10', 'Milano', '20100', '5555666677778888', '05/26', '123'),
('lucia', 'lucia@email.com', '1234', 'CLIENTE', 'Corso Italia 5', 'Napoli', '80100', '4444555566667777', '08/25', '456');

-- ==========================================
-- POPOLAMENTO DATI - SCARPE
-- ==========================================
INSERT INTO SCARPE (modello, marca, categoria, genere, taglia, prezzo, quantita, descrizione, url_immagine) VALUES
('Air Zoom Pegasus 40', 'NIKE', 'Corsa', 'UOMO', 42.5, 129.99, 15, 'La scarpa alata per eccellenza.', '/images/scarpa 1.png'),
('Invincible 3', 'NIKE', 'Corsa', 'UOMO', 43.0, 189.99, 8, 'Massima ammortizzazione per lunghe distanze.', '/images/scarpa 1.png'),
('React Infinity Run 4', 'NIKE', 'Corsa', 'DONNA', 38.0, 159.99, 12, 'Supporto morbido e reattivo.', '/images/nike_donna.jpg'),
('Pegasus Turbo Next Nature', 'NIKE', 'Corsa', 'DONNA', 39.0, 149.99, 5, 'Leggerezza e velocità sostenibile.', '/images/nike_donna.jpg'),
('LeBron XXI', 'NIKE', 'Basket', 'UOMO', 45.0, 199.99, 6, 'Costruita per il Re del campo.', '/images/nike_uomo.jpg'),
('KD16', 'NIKE', 'Basket', 'UOMO', 44.0, 159.99, 10, 'Per i giocatori che vogliono tutto.', '/images/nike_uomo.jpg'),
('G.T. Cut 3', 'NIKE', 'Basket', 'DONNA', 40.0, 189.99, 7, 'Progettata per creare spazio in velocità.', '/images/nike_donna.jpg'),
('G.T. Hustle 2', 'NIKE', 'Basket', 'DONNA', 38.5, 169.99, 4, 'Reattività per ogni scatto.', '/images/nike_donna.jpg'),
('Mercurial Superfly 9 Elite', 'NIKE', 'Calcio', 'UOMO', 42.0, 279.99, 5, 'Velocità esplosiva in campo.', '/images/scarpa 3.png'),
('Tiempo Legend 10 Elite', 'NIKE', 'Calcio', 'UOMO', 43.0, 249.99, 8, 'Tocco leggendario, pelle premium.', '/images/scarpa 3.png'),
('Phantom Luna Elite', 'NIKE', 'Calcio', 'DONNA', 39.0, 259.99, 6, 'Precisione e agilità per il calcio femminile.', '/images/scarpa 3.png'),
('Mercurial Vapor 15 Pro', 'NIKE', 'Calcio', 'DONNA', 37.5, 149.99, 10, 'Velocità accessibile.', '/images/scarpa 3.png'),
('Ultraboost Light', 'ADIDAS', 'Corsa', 'UOMO', 43.0, 180.00, 10, 'Energia epica, ora più leggera.', '/images/scarpa2.png'),
('Adizero Boston 12', 'ADIDAS', 'Corsa', 'UOMO', 42.5, 150.00, 8, 'Per il giorno della gara e l\'allenamento.', '/images/scarpa2.png'),
('Supernova Rise', 'ADIDAS', 'Corsa', 'DONNA', 38.0, 140.00, 15, 'Comfort quotidiano affidabile.', '/images/scarpa2.png'),
('Adizero SL', 'ADIDAS', 'Corsa', 'DONNA', 39.0, 120.00, 12, 'Velocità per tutti.', '/images/scarpa2.png'),
('Harden Vol. 7', 'ADIDAS', 'Basket', 'UOMO', 44.5, 160.00, 7, 'Stile unico e prestazioni MVP.', '/images/scarpa2.png'),
('Dame 8 EXTPLY', 'ADIDAS', 'Basket', 'UOMO', 43.0, 130.00, 9, 'Per i momenti decisivi.', '/images/scarpa2.png'),
('Exhibit Select', 'ADIDAS', 'Basket', 'DONNA', 40.0, 110.00, 8, 'Progettata specificamente per il piede femminile.', '/images/scarpa2.png'),
('Predator Elite', 'ADIDAS', 'Calcio', 'UOMO', 43.5, 249.99, 4, 'Controllo e potenza di tiro.', '/images/scarpa 3.png'),
('X Crazyfast.1', 'ADIDAS', 'Calcio', 'UOMO', 42.0, 219.99, 6, 'Leggerezza per la massima velocità.', '/images/scarpa 3.png'),
('Copa Pure.1', 'ADIDAS', 'Calcio', 'DONNA', 38.5, 229.99, 5, 'Tocco puro e comfort.', '/images/scarpa 3.png'),
('Deviate Nitro 2', 'PUMA', 'Corsa', 'UOMO', 43.0, 159.99, 10, 'Piastra in carbonio per la massima propulsione.', '/images/scarpa 1.png'),
('Magnify Nitro 2', 'PUMA', 'Corsa', 'UOMO', 42.0, 139.99, 8, 'Massima ammortizzazione NITRO.', '/images/scarpa 1.png'),
('Run XX Nitro', 'PUMA', 'Corsa', 'DONNA', 38.0, 129.99, 12, 'Progettata per la biomeccanica femminile.', '/images/scarpa 1.png'),
('Velocity Nitro 2', 'PUMA', 'Corsa', 'DONNA', 39.0, 119.99, 15, 'Ammortizzazione reattiva e versatile.', '/images/scarpa 1.png'),
('MB.03 LaMelo Ball', 'PUMA', 'Basket', 'UOMO', 45.0, 169.99, 3, 'Stile spaziale e performance.', '/images/scarpa2.png'),
('TRC Blaze Court', 'PUMA', 'Basket', 'UOMO', 43.0, 119.99, 6, 'Ispirata al running, fatta per il basket.', '/images/scarpa2.png'),
('Stewie 2', 'PUMA', 'Basket', 'DONNA', 40.0, 139.99, 5, 'La scarpa firmata Breanna Stewart.', '/images/scarpa2.png'),
('Future 7 Ultimate', 'PUMA', 'Calcio', 'UOMO', 42.5, 219.99, 7, 'Agilità creativa e calzata perfetta.', '/images/scarpa 3.png'),
('King Ultimate', 'PUMA', 'Calcio', 'UOMO', 43.0, 199.99, 9, 'Controllo classico, materiali moderni.', '/images/scarpa 3.png'),
('Ultra Match', 'PUMA', 'Calcio', 'DONNA', 38.0, 89.99, 10, 'Velocità accessibile, calzata femminile.', '/images/scarpa 3.png');

-- ==========================================
-- POPOLAMENTO RECENSIONI (METODO SICURO)
-- ==========================================

-- --- RECENSIONI NIKE ---
INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 5, 'Le Pegasus non deludono mai, ottime per allenarsi.'
FROM SCARPE WHERE modello = 'Air Zoom Pegasus 40';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'lucia', 4, 'Molto comode, ma preferisco una scarpa più secca.'
FROM SCARPE WHERE modello = 'React Infinity Run 4';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 5, 'LeBron è il Re, scarpa incredibile!'
FROM SCARPE WHERE modello = 'LeBron XXI';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 4, 'Ottima trazione, un po pesanti.'
FROM SCARPE WHERE modello = 'Mercurial Vapor 15 Pro';

-- --- RECENSIONI ADIDAS ---
INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 5, 'Ultraboost comodissime, come camminare sulle nuvole!'
FROM SCARPE WHERE modello = 'Ultraboost Light';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'lucia', 4, 'Ottime ma costano un po troppo.'
FROM SCARPE WHERE modello = 'Ultraboost Light';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'seller', 5, 'Adizero perfette per la maratona.'
FROM SCARPE WHERE modello = 'Adizero Boston 12';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 3, 'Harden belle ma un po pesanti.'
FROM SCARPE WHERE modello = 'Harden Vol. 7';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'lucia', 5, 'Stile unico, in campo si notano.'
FROM SCARPE WHERE modello = 'Harden Vol. 7';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 4, 'Predator classiche, tocco di palla eccellente.'
FROM SCARPE WHERE modello = 'Predator Elite';

-- --- RECENSIONI PUMA ---
INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 4, 'Puma Deviate molto reattive.'
FROM SCARPE WHERE modello = 'Deviate Nitro 2';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'lucia', 5, 'Rapporto qualità prezzo imbattibile.'
FROM SCARPE WHERE modello = 'Deviate Nitro 2';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 5, 'Le scarpe di LaMelo sono spaziali!'
FROM SCARPE WHERE modello = 'MB.03 LaMelo Ball';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'lucia', 3, 'Belle ma calzano un po strette.'
FROM SCARPE WHERE modello = 'Ultra Match';

INSERT INTO RECENSIONE (id_scarpa, username_utente, voto, testo)
SELECT idSCARPA, 'mario', 5, 'King Ultimate: pelle sintetica fantastica.'
FROM SCARPE WHERE modello = 'King Ultimate';