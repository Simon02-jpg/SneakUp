package com.sneakup.pattern.observer;

import com.sneakup.model.domain.Ordine;

public interface Observer {
    void update(Ordine ordine);
}