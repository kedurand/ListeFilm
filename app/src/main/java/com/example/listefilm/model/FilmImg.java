package com.example.listefilm.model;

import android.graphics.Bitmap;

import com.example.listefilm.utility.ImageUtil;
import com.orm.SugarRecord;

public class FilmImg extends SugarRecord {
    private Film film;
    // Impossible de sauvegarder une image sous forme Bitmap sous SugarORM
    // On va donc le convertir en array de byte puis le convertir en bitmap à l'usage
    private byte[] img;

    public FilmImg() {
    }

    public FilmImg(Film film, byte[] img) {
        this.film = film;
        this.img = img;
    }

    public FilmImg(Film film) {
        this.film = film;
    }

    public FilmImg(byte[] img) {
        this.img = img;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Bitmap getImg() {
        return ImageUtil.convertByteArrayToBitmap(this.img);
    }

    public void setImg(Bitmap img) {
        this.img = ImageUtil.GetByteFromBitmap(img);
    }
}
