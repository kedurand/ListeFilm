package com.example.listefilm.model;

import android.graphics.Bitmap;

import com.orm.SugarRecord;

public class Film extends SugarRecord{
    private Bitmap  image;
    private String  titre;
    private String  realisateur;
    private String  producteur;
    private String  annee;

    public Film(){}

    public Film(Bitmap image, String titre, String realisateur, String producteur, String annee) {
        this.image = image;
        this.titre = titre;
        this.realisateur = realisateur;
        this.producteur = producteur;
        this.annee = annee;
    }

    public Film(String titre, String realisateur, String producteur, String annee) {
        this.titre = titre;
        this.realisateur = realisateur;
        this.producteur = producteur;
        this.annee = annee;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public String getProducteur() {
        return producteur;
    }

    public void setProducteur(String producteur) {
        this.producteur = producteur;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }
}
