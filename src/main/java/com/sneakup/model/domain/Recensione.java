package com.sneakup.model.domain;

import java.sql.Timestamp;

public class Recensione {
    private int id;
    private int idScarpa;
    private String username;
    private int voto;
    private String testo;
    private Timestamp dataInserimento;

    public Recensione() {}

    public Recensione(int id, int idScarpa, String username, int voto, String testo, Timestamp dataInserimento) {
        this.id = id;
        this.idScarpa = idScarpa;
        this.username = username;
        this.voto = voto;
        this.testo = testo;
        this.dataInserimento = dataInserimento;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdScarpa() { return idScarpa; }
    public void setIdScarpa(int idScarpa) { this.idScarpa = idScarpa; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public Timestamp getDataInserimento() { return dataInserimento; }
    public void setDataInserimento(Timestamp dataInserimento) { this.dataInserimento = dataInserimento; }
}