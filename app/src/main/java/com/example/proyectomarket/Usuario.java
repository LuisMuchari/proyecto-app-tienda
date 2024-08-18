package com.example.proyectomarket;

import java.io.Serializable;

public class Usuario implements Serializable {
    private int id;
    private String nom;
    private String cod;

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setId(int id) {
        this.id = id;
    }
}
