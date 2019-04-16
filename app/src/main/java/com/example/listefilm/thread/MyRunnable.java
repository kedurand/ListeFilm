package com.example.listefilm.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.example.listefilm.adapter.FilmAdapter;
import com.example.listefilm.model.Film;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Utilisation pour des treatement synchrone avec l'utilisateur
// Constitue un travail à faire dans un Thread
// On obtient de meilleurs performance qu'avec le AsyncTask
// Son cousin : Callable permet de rendre un résultat dans l'objet Future
public class MyRunnable implements Runnable {
    private Film film;
    // Les weakreference ne concerne que les objets graphiques
    private WeakReference<FilmAdapter> wkAdapter;
    // Il nous faut une strong reference dans nos méthode pour manipuler l'objet
    private FilmAdapter adapter;
    private String url;
    private Handler handlerUI;

    public MyRunnable(Film film, FilmAdapter adapter, String url, Handler handlerUI) {
        this.film = film;
        this.wkAdapter = new WeakReference<FilmAdapter>(adapter) ;
        this.url = url;
        this.handlerUI = handlerUI;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            Bitmap btm = this.downloadBitmapFromURL(this.url);
            this.film.setImage(btm);

            // Pour manipuler notre adaptateur, il faut le recupérer depuis notre weakref
            // On fait une strong référence depuis la weak
            this.adapter = this.wkAdapter.get();
            // On s'assure qu'il n'est pas déjà mort avant de le manipuler
            if(this.adapter != null){
                // Poste le travail graphique à effectuer dans le Handler Thread UI
                // Car c'est le seul abilité à le faire dans la hiérarchie Android
                this.handlerUI.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Bitmap downloadBitmapFromURL(String url) {
        URL monURL = null;
        HttpURLConnection urlConnection = null;
        Bitmap bmp = null;

        try {
            monURL = new URL(url);
            urlConnection = (HttpURLConnection) monURL.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bmp = BitmapFactory.decodeStream(in);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bmp;
    }
}