package com.sneakup.model.domain; // 1. Package corretto (con "com.")

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CarrelloTest {

    private Carrello carrello;

    @BeforeEach
    void setUp() {
        // Inizializza un nuovo carrello prima di ogni test
        carrello = new Carrello();
    }

    @Test
    void testAggiungiScarpa() {
        // 2. Costruttore corretto in base alla tua classe Scarpa:
        // (Modello, Marca, Categoria, Taglia, Prezzo, Quantità)
        Scarpa s = new Scarpa("Air Max", "Nike", "Running", 42.0, 100.0, 10);

        // Se vuoi impostare ID, immagine o descrizione, usa i setter:
        s.setId(1);
        s.setDescrizione("Scarpa da test");

        carrello.aggiungiScarpa(s);

        assertEquals(1, carrello.getScarpeSelezionate().size(), "Il carrello dovrebbe contenere 1 scarpa");
        assertEquals(100.0, carrello.getTotale(), "Il totale dovrebbe essere 100.0");
    }

    @Test
    void testRimuoviScarpa() {
        // Creazione scarpe usando il costruttore vuoto e i setter (più sicuro)
        Scarpa s1 = new Scarpa();
        s1.setId(1);
        s1.setModello("A");
        s1.setPrezzo(50.0);

        Scarpa s2 = new Scarpa();
        s2.setId(2);
        s2.setModello("B");
        s2.setPrezzo(50.0);

        carrello.aggiungiScarpa(s1);
        carrello.aggiungiScarpa(s2);

        // Verifica rimozione
        carrello.rimuoviScarpa(s1);

        assertEquals(1, carrello.getScarpeSelezionate().size(), "Dovrebbe rimanere 1 scarpa");
        assertEquals(50.0, carrello.getTotale(), "Il totale dovrebbe essere 50.0");
        assertEquals(s2, carrello.getScarpeSelezionate().get(0), "La scarpa rimasta dovrebbe essere la s2");
    }
}