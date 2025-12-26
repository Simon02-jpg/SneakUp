package com.sneakup.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CarrelloTest {

    private Carrello carrello;

    @BeforeEach
    void setUp() {
        carrello = Carrello.getInstance();
        carrello.svuotaCarrello(); // Pulizia prima di ogni test
    }

    @Test
    void testAggiungiScarpa() {
        Scarpa s = new Scarpa("Test", "Nike", "Run", 42.0, 100.0, 10);
        carrello.aggiungiScarpa(s);

        assertEquals(1, carrello.getScarpeSelezionate().size(), "Il carrello dovrebbe contenere 1 scarpa");
        assertEquals(100.0, carrello.getTotale(), "Il totale dovrebbe essere 100.0");
    }

    @Test
    void testRimuoviScarpa() {
        Scarpa s1 = new Scarpa("A", "N", "R", 40, 50.0, 1);
        Scarpa s2 = new Scarpa("B", "N", "R", 40, 50.0, 1);

        carrello.aggiungiScarpa(s1);
        carrello.aggiungiScarpa(s2);

        carrello.rimuoviScarpa(s1);

        assertEquals(1, carrello.getScarpeSelezionate().size());
        assertEquals(50.0, carrello.getTotale());
    }

    @Test
    void testSingleton() {
        Carrello c1 = Carrello.getInstance();
        Carrello c2 = Carrello.getInstance();

        assertSame(c1, c2, "Il carrello deve essere un Singleton (stessa istanza)");
    }
}