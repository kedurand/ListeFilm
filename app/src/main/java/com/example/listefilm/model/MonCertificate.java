package com.example.listefilm.model;

import com.orm.SugarRecord;

import java.io.Serializable;

// Object doit être sérializable pour être passé en objectOutputStream
public class MonCertificate extends SugarRecord implements Serializable{
    private String contenu;

    public MonCertificate() {
    }

    public MonCertificate(String contenu) {
        this.contenu = contenu;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
