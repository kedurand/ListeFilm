package com.example.listefilm.asynctask;

import android.os.AsyncTask;

import com.example.listefilm.adapter.FilmAdapter;
import com.example.listefilm.model.Film;
import com.example.listefilm.model.FilmImg;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AT_GetFilmIMDB extends AsyncTask<String, Integer, FilmImg> {
    private static final String URL_API_KEY = "743ae636";
    private static String URL_API = "http://www.omdbapi.com/?apikey=";

    private WeakReference<List<FilmImg>> wkFilmList;
    private List<FilmImg>  filmList;
    private WeakReference<FilmAdapter> wkAdapter;
    private FilmAdapter adapter;

    public AT_GetFilmIMDB(List<FilmImg> filmList, FilmAdapter adapter) {
        this.wkFilmList = new WeakReference<>(filmList);
        this.wkAdapter = new WeakReference<>(adapter);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected FilmImg doInBackground(String... strings) {
        // Récupération de la réponse JSON de l'appel de l'API avec ID et KEY
        final String responseJSON = this.getFicheFilmOMDB(strings[0], AT_GetFilmIMDB.URL_API_KEY);
        // Construction d'un objet Gson pour faire convertir le JSON en objet
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.setPrettyPrinting().create();
        // Retourne une instance d'objet d'après le texte JSON
        Film film = gson.fromJson(responseJSON, Film.class);
        // Retourne une instance FilmImg
        return new FilmImg(film, null);
    }

    @Override
    protected void onPostExecute(FilmImg film) {
        super.onPostExecute(film);
        try{
            // Sauvegarde le film, important car sinon disparait
            film.getFilm().save();
            // Pareil pour le filmIMG dans BDD SQLite
            film.save();
            // Actualisation de la liste des films et notification de l'adapter
            this.filmList = this.wkFilmList.get();
            this.filmList.clear();
            this.filmList.addAll(FilmImg.listAll(FilmImg.class));
            this.adapter = this.wkAdapter.get();
            this.adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // Appel de l'API selon l'id et notre key pour avoir les information IMDB d'un film
    private String getFicheFilmOMDB(String id, String key){
        // Création de l'URL via les placeholder
        String url = AT_GetFilmIMDB.URL_API + AT_GetFilmIMDB.URL_API_KEY + "&i=" + id;
        StringBuilder sbuilder;
        BufferedReader reader = null;

        try {
            URL monURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) monURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Récupération de la réponse de requête
            sbuilder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while( (line = reader.readLine()) != null){
                sbuilder.append(line);
            }

            return sbuilder.toString();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(reader != null){
                try{
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
