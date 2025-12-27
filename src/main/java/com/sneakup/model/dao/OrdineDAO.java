package com.sneakup.model.dao;

import com.sneakup.model.domain.Ordine;
import com.sneakup.exception.SneakUpException;
import java.util.List;

public interface OrdineDAO {
    void salvaOrdine(Ordine ordine) throws SneakUpException;
    List<Ordine> getAllOrdini() throws SneakUpException;
}