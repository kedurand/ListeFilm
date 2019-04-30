package com.example.listefilm.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.listefilm.adapter.SearchAdapter;
import com.example.listefilm.model.Search;
import com.example.listefilm.model.SearchImg;
import com.example.listefilm.model.SearchList;
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

public class AT_SearchIMDB extends AsyncTask<String, Integer, List<Search>> {
    private static final String URL_API_KEY = "743ae636";
    private static String URL_API = "http://www.omdbapi.com/?apikey=";

    private WeakReference<List<SearchImg>> wkSearchList;
    private WeakReference<SearchAdapter> wkAdapter;

    public AT_SearchIMDB(List<SearchImg> searchImg, SearchAdapter adapter) {
        this.wkSearchList = new WeakReference<>(searchImg);
        this.wkAdapter = new WeakReference<>(adapter);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Search> doInBackground(String... strings) {
        // Récupération de la réponse JSON de l'appel de l'API avec ID et KEY
        final String responseJSON = this.getFicheFilmOMDB(strings[0], AT_SearchIMDB.URL_API_KEY);
        // Construction d'un objet Gson pour faire convertir le JSON en objet
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.setPrettyPrinting().create();
        // Retourne une instance d'objet d'après le texte JSON
        SearchList searchList = gson.fromJson(responseJSON, SearchList.class);

        return searchList.getSearch();
    }

    @Override
    protected void onPostExecute(List<Search> searchList) {
        super.onPostExecute(searchList);
        try{
            // Clean de la liste de searchImg destiné à la listeview
            List<SearchImg> searchImgList = this.wkSearchList.get();
            searchImgList.clear();
            // Récupération de la strong value de l'adapter
            SearchAdapter adapter = this.wkAdapter.get();

            // Si aucun résultat, crée un film vide...
            if (searchList == null  || searchList.isEmpty()){
                Search search = new Search();
                search.setTitle("Pas de résultat pour la recherche....");
                SearchImg searchImg = new SearchImg(search, (Bitmap) null);
                searchImgList.add(searchImg);
            }
            else{
                // On va boucler sur chaque item de la liste et télécharger l'image
                for(Search search : searchList){
                    // Création de l'objet search avec une possible image
                    SearchImg searchImg = new SearchImg(search, (Bitmap) null);
                    // Téléchargement de l'image d'après l'URL et set dans l'objet
                    // Avec l'aide d'une async task
                    AT_SearchDownloadImg  at_searchDownloadImg = new AT_SearchDownloadImg(searchImg,
                            adapter);
                    // Le set de l'image dans l'objet se fait dans l'AT tout comme le notify data change
                    at_searchDownloadImg.execute(search.getPoster());
                    // Ajout dans la liste
                    searchImgList.add(searchImg);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // Appel de l'API selon l'id et notre key pour avoir les information IMDB d'un film
    private String getFicheFilmOMDB(String value, String key){
        // Exemple de recherche de film
        // http://www.omdbapi.com/?apikey=743ae636&s=Avengers
        String url = AT_SearchIMDB.URL_API + AT_SearchIMDB.URL_API_KEY + "&s=" + value;

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
