package com.example.listefilm.model;

import com.orm.SugarRecord;

public class MonCertificate extends SugarRecord {
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
