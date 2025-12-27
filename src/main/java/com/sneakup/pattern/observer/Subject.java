package com.sneakup.pattern.observer;

import com.sneakup.model.domain.Ordine;

public interface Subject {
    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers(Ordine ordine);
}