package com.example.listefilm.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.listefilm.adapter.FilmAdapter;
import com.example.listefilm.adapter.SearchAdapter;
import com.example.listefilm.model.FilmImg;
import com.example.listefilm.model.SearchImg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

// On utilise une AsyncTask pour traiter des objets graphique lorsque l'on pas
// pas de besoin conversationnel avec ce dernier, l'on peut donc le faire de manière asynchrone
// Permet de récupérer une image depuis une URL via une connexion
// Ne pas oublier la permission internet dans le manifest
// Récupération d'une image du buffer de la connexion en bitmap
/*
    Il est très conseillé de ne pas faire de classe à l'interieur d'une classe
    On a toujours un référencement implicite de la classe externe dans la classe interne
    Le thread interne aura donc toujours une référence forte à la classe externe
    Il ne sera donc pas garbage collecté et va saturer la mémoire et crasher l'application
    Il est possible de garbage collecté un groupe d'objet qui se reférénce entre eux mais qui
    n'est pas référencé par autre chose (mais compliqué)
 */
public class AT_SearchDownloadImg extends AsyncTask<String, Integer, Bitmap> {
    // On veut avoir une référence sur un objet graphique d'un autre thread afin de pouvoir
    // informer l'utilisateur
    // Weakreference: permet de rendre null la référence s'il n'y a plus de strong references
    //                pointant dessus. Si count(Strong Reference) = 0 => WeakReference meurt
    // Au lieu de faire "a = b" => WeakReference<T> a = Weakreference (b)
    private WeakReference<SearchImg> wkSearchImg;
    private WeakReference<SearchAdapter> wkAdapter;

    public AT_SearchDownloadImg(SearchImg searchImg, SearchAdapter adapter) {
        this.wkSearchImg = new WeakReference<>(searchImg);
        this.wkAdapter = new WeakReference<>(adapter);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            // Permet de voir la parallélisation ou le séquentiel
            Thread.sleep(1000);
            return this.downloadBitmapFromURL(urls[0]);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // Permet de lancer l'exécution du code en s'assurant
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        SearchImg searchImg = this.wkSearchImg.get();
        searchImg.setImg(bitmap);

        // Récupération d'une strong reference de la weak reference pour manipuler l'objet graphique
        SearchAdapter adapter = this.wkAdapter.get();
        // Vérifie que la weak reference n'est pas déjà morte
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private Bitmap downloadBitmapFromURL(String url){
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
