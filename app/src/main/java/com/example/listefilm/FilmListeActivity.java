package com.example.listefilm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.listefilm.adapter.FilmAdapter;
import com.example.listefilm.asynctask.MyAsyncTask;
import com.example.listefilm.handler.MyHandlerThreadMessage;
import com.example.listefilm.model.Film;
import com.example.listefilm.thread.MyRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FilmListeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btAjout;
    private Button btSupprTout;
    private Button btAsyncS;
    private Button btAsyncP;
    private Button btThreads;
    private Button btHandlerTR;
    private Button btHandlerTM;
    private Button btPool;
    private ListView listView;

    private final String sImgURL = "http://lorempixel.com/400/400/";

    public  List<Film>  filmList;
    private FilmAdapter adapter;

    // Un Handler UI tourne constamment en Android et est le seul a être abilité de toucher
    // aux objets graphique. On doit donc déposer du travail directement dans sa queue via post()
    private Handler handlerUI;
    // On va créer un handler thread avec sa propre queue pour effectuer tout autre traitement
    private HandlerThread handlerThread;
    // On crée une référence d'handler pour poster des travail dessus
    private Handler handlerFilm;
    // On crée un handler thread prenant en compte les messages
    // Todo en faire une nouvelle classe qui implements Handlercallbak
    private MyHandlerThreadMessage handlerThreadMessage;
    // Récupère un handler pour gérer le queue du HandlerThread
    private Handler handlerFilmMessage;
    // Pool de thread pour gérer la quantité de thread exécuté
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filmliste);

        this.initialize();
        this.loadingListView();
    }

    private void initialize(){
        //Enregistrer les boutons pour le listener
        this.btAjout = (Button) findViewById(R.id.bt_ajout);
        this.btAjout.setOnClickListener(this);
        this.btSupprTout = (Button) findViewById(R.id.bt_supprTout);
        this.btSupprTout.setOnClickListener(this);
        this.btAsyncS = (Button) findViewById(R.id.bt_asyncS);
        this.btAsyncS.setOnClickListener(this);
        this.btAsyncP = (Button) findViewById(R.id.bt_asyncP);
        this.btAsyncP.setOnClickListener(this);
        this.btThreads = (Button) findViewById(R.id.bt_threads);
        this.btThreads.setOnClickListener(this);
        this.btHandlerTR = (Button) findViewById(R.id.bt_handlerTR);
        this.btHandlerTR.setOnClickListener(this);
        this.btHandlerTM = (Button) findViewById(R.id.bt_handlerTM);
        this.btHandlerTM.setOnClickListener(this);
        this.btPool  = (Button) findViewById(R.id.bt_pool);
        this.btPool.setOnClickListener(this);
        this.listView = (ListView)findViewById(R.id.lv_filmList);

        // On récupère le Handler UI pour déposer du travail dessus
        this.handlerUI = new Handler(Looper.getMainLooper());

        // Handler pour les travaux
        this.handlerThread = new HandlerThread("HandlerThreadFilm");
        this.handlerThread.start();
        // On récupère le Handler Perso pour intéragir avec
        this.handlerFilm = new Handler(this.handlerThread.getLooper());

        // On créer un handler thread pour les messages
        this.handlerThreadMessage = new MyHandlerThreadMessage("HandlerThreadFilmMessage");
        this.handlerThreadMessage.start();
        // On recupère le handler message
        this.handlerFilmMessage = new Handler(this.handlerThreadMessage.getLooper());

        this.threadPoolExecutor = new ThreadPoolExecutor(2,4,
                                                        100, TimeUnit.SECONDS,
                                                        new LinkedBlockingDeque<Runnable>());
    }

    private void loadingListView(){
        this.filmList = new ArrayList<>();
        this.filmList.addAll(Film.listAll(Film.class));
        this.adapter = new FilmAdapter(getApplicationContext(), this.filmList);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new ListClickHandler());
    }

    // Recharge la List avec ce qui est dans la BD
    private void refreshListView(){
        this.filmList.clear();
        this.filmList.addAll(Film.listAll(Film.class));
        this.adapter.notifyDataSetChanged();
    }

    // Permet d'ajouter un film
    private void ajoutFilm(){
        Film film = new Film("TitreTest", "RealisateurTest",
                        "ProducteurTest", "AnneeTest");
        try{
            //Création du contact avec ORM, save retourne un long pour gestion erreur
            film.save();
            this.refreshListView();
            Toast.makeText(this, film.getTitre() + " " + film.getRealisateur() + "\n"
                                         + "Film bien enregistré",Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void supprTout(){
        try {
            Film.deleteAll(Film.class);
            this.refreshListView();
            Toast.makeText(this, "Tous les films ont été supprimé",
                            Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    // Utilisation des asynctask particulière et conseillé pour les thread graphique en Android
    // Boucle sur chaque film pour ajouter une image téléchargée depuis l'URL
    // L'execution se fait séquentiellement, on lance un thread par film
    private void asyncS(){
        MyAsyncTask myAsyncTask = null;

        for(Film film : this.filmList) {
            myAsyncTask = new MyAsyncTask(film, this.adapter);
            myAsyncTask.execute(this.sImgURL);
        }
    }

    // On veut faire une exécution de aSyncTask en parallèle
    private void asyncP(){
        MyAsyncTask myAsyncTask = null;

        for(Film film : this.filmList) {
            myAsyncTask = new MyAsyncTask(film, this.adapter);
            myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.sImgURL);
        }
    }

    // Génération non limitée de thread -> déconseillé car prend toute les ressources dispo
    private void threads(){
        Thread thread = null;

        for(Film film : this.filmList) {
            thread = new Thread(new MyRunnable(film, this.adapter, this.sImgURL, this.handlerUI));
            // Il est important d'utiliser start() pour lancer un nouveau thread
            // Si l'on fait un simple run(), il va juste run le thread courant
            thread.start();
        }
    }

    // Un Handler Thread est un thread qui exécute un seul code dans sa vie
    // Il contient un looper, il prend un travail dans la queue, il l'exécute et ainsi de suite
    // On dépose un travail dans la queue du handler (utilisation de la méthode post)
    // On peut aussi bien déposer un Runnable qu'un message dont on doit spécifier l'action associé
    // Permet d'alléger le main Handler UI
    private void handlerTR(){
        for(Film film : this.filmList) {
            this.handlerFilm.post(new MyRunnable(film, this.adapter, this.sImgURL, this.handlerUI));
        }
    }

    // Un Handler Thread Message fonctionne comme un Handler Thread classique
    // Il permet, en plus, le post de Message qui sont des évenement
    // Le handler devra donc pouvoir réagir en fonction du type de message
    // On peut passer des objets directement dans le message
    private void handlerTM(){
        for(Film film : this.filmList) {
            // Au lieu de recréer un message à chaque film
            // on va obtenir un message déjà existant si possible
            Message msg = Message.obtain();
            // Donne le type depuis l'énumération de la casse du type de message
            msg.what = MyHandlerThreadMessage.iMsgType_DownloadImg;
            // Lui passe un objet en paramètre, devoir faire un objet pour englober tout
            msg.obj = this.adapter;
            // Post un message dans la queue du handler sous forme runnable
            this.handlerFilmMessage.post(msg.getCallback());
        }
    }

    // On veut maitriser le nombre de thread simultanné exécuté dans un pool
    // Même si on met 4 en nombre de pool initial, s'il y a un coeur du CPU qui est en veille
    // L'on aura pas les 4 pool initial
    private void pool(){
        for(Film film : this.filmList) {
            this.threadPoolExecutor.execute(new MyRunnable(film, this.adapter,this.sImgURL, this.handlerUI));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_ajout:
                this.ajoutFilm();
                break;
            case R.id.bt_supprTout:
                this.supprTout();
                break;
            case R.id.bt_asyncS:
                this.asyncS();
                break;
            case R.id.bt_asyncP:
                this.asyncP();
                break;
            case R.id.bt_threads:
                this.threads();
                break;
            case R.id.bt_handlerTR:
                this.handlerTR();
                break;
            case R.id.bt_handlerTM:
                this.handlerTM();
                break;
            case R.id.bt_pool:
                this.pool();
                break;
            default:
                break;
        }
    }

    // On veut pouvoir supprimer un film directement avec une action dessus
    private class ListClickHandler implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    }
}
