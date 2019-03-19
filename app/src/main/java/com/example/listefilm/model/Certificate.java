package com.example.listefilm.model;

import com.orm.SugarRecord;

public class Certificate extends SugarRecord {
    private String contenu;

    public Certificate() {
    }

    public Certificate(String contenu) {
        this.contenu = contenu;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
