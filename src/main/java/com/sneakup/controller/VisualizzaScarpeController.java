package com.sneakup.controller;

import com.sneakup.exception.SneakUpException;
import com.sneakup.model.dao.DAOFactory;
import com.sneakup.model.domain.Scarpa;
import java.util.List;

public class VisualizzaScarpeController {

    public List<Scarpa> getTutteLeScarpe() throws SneakUpException {
        return DAOFactory.getInstance().getScarpaDAO().getAllScarpe();
    }

    // NUOVO
    public void eliminaScarpa(int idScarpa) throws SneakUpException {
        DAOFactory.getInstance().getScarpaDAO().deleteScarpa(idScarpa);
    }
}