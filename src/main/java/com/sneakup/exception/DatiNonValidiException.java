package com.sneakup.exception;

public class DatiNonValidiException extends SneakUpException {
    public DatiNonValidiException(String message) {
        super("Errore Validazione: " + message);
    }
}