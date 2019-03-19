package com.example.listefilm.model;

import android.os.Handler;

import com.example.listefilm.adapter.FilmAdapter;

public class ParamThread {
    private FilmAdapter adapter;
    private Film film;
    private String url;
    private Handler handlerUI;

    public ParamThread(Film film, FilmAdapter adapter , String url, Handler handlerUI) {
        this.film = film;
        this.adapter = adapter;
        this.url = url;
        this.handlerUI = handlerUI;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public FilmAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FilmAdapter adapter) {
        this.adapter = adapter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Handler getHandlerUI() {
        return handlerUI;
    }

    public void setHandlerUI(Handler handlerUI) {
        this.handlerUI = handlerUI;
    }
}
