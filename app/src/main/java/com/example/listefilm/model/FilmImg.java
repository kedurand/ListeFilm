package com.example.listefilm.model;

import android.graphics.Bitmap;

import com.orm.SugarRecord;

public class FilmImg extends SugarRecord {
    private Film film;
    private Bitmap img;

    public FilmImg() {
    }

    public FilmImg(Film film, Bitmap img) {
        this.film = film;
        this.img = img;
    }

    public FilmImg(Film film) {
        this.film = film;
    }

    public FilmImg(Bitmap img) {
        this.img = img;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
