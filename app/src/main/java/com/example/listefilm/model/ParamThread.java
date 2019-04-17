package com.example.listefilm.model;

import android.os.Handler;

import com.example.listefilm.adapter.FilmAdapter;

public class ParamThread {
    private FilmAdapter adapter;
    private FilmImg filmImg;
    private String url;
    private Handler handlerUI;

    public ParamThread(FilmImg film, FilmAdapter adapter , String url, Handler handlerUI) {
        this.filmImg = film;
        this.adapter = adapter;
        this.url = url;
        this.handlerUI = handlerUI;
    }

    public FilmImg getFilmImg() {
        return filmImg;
    }

    public void setFilmImg(FilmImg film) {
        this.filmImg = film;
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
