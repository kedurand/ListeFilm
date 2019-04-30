package com.example.listefilm.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.example.listefilm.adapter.FilmAdapter;
import com.example.listefilm.model.FilmImg;

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
    /*
        On choisit des weakreference lorsque l'on sait que cet objet est suceptible de disparaitre
        Les strong sont pour les objets qui sont persistant (ex: HandlerUI)
    */
    private WeakReference<FilmImg> wkFilm;
    // Les weakreference ne concerne que les objets graphiques
    private WeakReference<FilmAdapter> wkAdapter;
    // Il nous faut une strong reference dans nos méthode pour manipuler l'objet
    private FilmAdapter adapter;
    private String url;
    // Weakreference à l'handler principal du programme
    private WeakReference<Handler> wkHandlerUI;

    public MyRunnable(FilmImg film, FilmAdapter adapter, String url, Handler handlerUI) {
        this.wkFilm = new WeakReference<>(film);
        this.wkAdapter = new WeakReference<>(adapter) ;
        this.url = url;
        this.wkHandlerUI = new WeakReference<>(handlerUI);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            Bitmap btm = this.downloadBitmapFromURL(this.url);
            FilmImg film = this.wkFilm.get();
            film.setImg(btm);

            // Pour manipuler notre adaptateur, il faut le recupérer depuis notre weakref
            // On fait une strong référence depuis la weak
            this.adapter = this.wkAdapter.get();
            // On s'assure qu'il n'est pas déjà mort avant de le manipuler
            if(this.adapter != null){
                // Poste le travail graphique à effectuer dans le Handler Thread UI
                // Car c'est le seul abilité à le faire dans la hiérarchie Android
                Handler handlerUI = this.wkHandlerUI.get();
                handlerUI.post(new Runnable() {
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
        URL monURL;
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