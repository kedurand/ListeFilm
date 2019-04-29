package com.example.listefilm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.listefilm.adapter.FilmAdapter;
import com.example.listefilm.asynctask.AT_GetFilmIMDB;
import com.example.listefilm.asynctask.MyAsyncTask;
import com.example.listefilm.handler.MyHandlerThreadMessage;
import com.example.listefilm.model.FilmImg;
import com.example.listefilm.model.ParamThread;
import com.example.listefilm.thread.MyRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FilmListeActivity extends AppCompatActivity implements View.OnClickListener{
    private final Context context = this;

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

    public  List<FilmImg>  filmList;
    private FilmAdapter adapter;

    // L'ID demandée à l'utilisateur pour le film
    private String filmID;

    // Un Handler UI tourne constamment en Android et est le seul a être abilité de toucher
    // aux objets graphique. On doit donc déposer du travail directement dans sa queue via post()
    private Handler handlerUI;
    // On va créer un handler thread avec sa propre queue pour effectuer tout autre traitement
    private HandlerThread handlerThread;
    // On crée une référence d'handler pour poster des travail dessus
    private Handler handlerFilm;
    // On crée un handler thread prenant en compte les messages
    private HandlerThread handlerThreadMessage;
    // Récupère un handler pour gérer le queue du HandlerThread
    private Handler handlerFilmMessage;
    // Pool de thread pour gérer la quantité de thread exécuté
    private ThreadPoolExecutor threadPoolExecutor;

    // Gestion des permissions
    // Cette variable permet de savoir quelle permission est demandé à l'utilisateur
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filmliste);

        this.initialize();
        this.loadingListView();
    }

    private void initialize(){
        //Enregistrer les boutons pour le listener
        this.btAjout = findViewById(R.id.bt_ajout);
        this.btAjout.setOnClickListener(this);
        this.btSupprTout = findViewById(R.id.bt_supprTout);
        this.btSupprTout.setOnClickListener(this);
        this.btAsyncS = findViewById(R.id.bt_asyncS);
        this.btAsyncS.setOnClickListener(this);
        this.btAsyncP = findViewById(R.id.bt_asyncP);
        this.btAsyncP.setOnClickListener(this);
        this.btThreads = findViewById(R.id.bt_threads);
        this.btThreads.setOnClickListener(this);
        this.btHandlerTR = findViewById(R.id.bt_handlerTR);
        this.btHandlerTR.setOnClickListener(this);
        this.btHandlerTM = findViewById(R.id.bt_handlerTM);
        this.btHandlerTM.setOnClickListener(this);
        this.btPool  = findViewById(R.id.bt_pool);
        this.btPool.setOnClickListener(this);
        this.listView = findViewById(R.id.lv_filmList);

        // On récupère le Handler UI pour déposer du travail dessus
        this.handlerUI = new Handler(Looper.getMainLooper());

        // Handler pour les travaux
        this.handlerThread = new HandlerThread("HandlerThreadFilm");
        this.handlerThread.start();
        // On récupère le Handler Perso pour intéragir avec
        this.handlerFilm = new Handler(this.handlerThread.getLooper());

        // On créer un handler thread classique
        this.handlerThreadMessage = new HandlerThread("HandlerThreadFilmMessage");
        this.handlerThreadMessage.start();
        // On recupère le handler pour le manipuler en ajoutant le handler thread message
        this.handlerFilmMessage = new Handler(  this.handlerThreadMessage.getLooper(),
                                                new MyHandlerThreadMessage());

        this.threadPoolExecutor = new ThreadPoolExecutor(2,4,
                                                        100, TimeUnit.SECONDS,
                                                        new LinkedBlockingDeque<Runnable>());

        // Gestion des permissions
        // NE SURTOUT PAS OUBLIER DE LES DECLARER DANS LE MANIFEST
        // Attention ne fonctionne pas en version API 5 ou moins
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Demande à l'utilisateur la permission si pas déjà donné
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void loadingListView(){
        this.filmList = new ArrayList<>();
        this.filmList.addAll(FilmImg.listAll(FilmImg.class));
        this.adapter = new FilmAdapter(this.getApplicationContext(), this.filmList);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new ListClickHandler());
    }

    // Recharge la List avec ce qui est dans la BD
    private void refreshListView(){
        this.filmList.clear();
        this.filmList.addAll(FilmImg.listAll(FilmImg.class));
        this.adapter.notifyDataSetChanged();
    }

    // Permet d'ajouter un film
    private void ajoutFilm(String id){
        /*
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
        */

        AT_GetFilmIMDB at_getFilmIMDB = new AT_GetFilmIMDB(this.filmList, this.adapter);
        // lance la requête API pour récupérer le film et actualiser la liste !
        at_getFilmIMDB.execute(id);
    }

    private void supprTout(){
        try {
            FilmImg.deleteAll(FilmImg.class);
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
        MyAsyncTask myAsyncTask;

        for(FilmImg film : this.filmList) {
            myAsyncTask = new MyAsyncTask(film, this.adapter);
            //myAsyncTask.execute(this.sImgURL);
            // On va utiliser l'URL fournit dans la fiche film pour télécahrger la couverture
            myAsyncTask.execute(film.getFilm().getPoster());
        }
    }

    // On veut faire une exécution de aSyncTask en parallèle
    private void asyncP(){
        MyAsyncTask myAsyncTask;

        for(FilmImg film : this.filmList) {
            myAsyncTask = new MyAsyncTask(film, this.adapter);
            // myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.sImgURL);
            // On va utiliser l'URL fournit dans la fiche film pour télécahrger la couverture
            myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, film.getFilm().getPoster());
        }
    }

    // Génération non limitée de thread -> déconseillé car prend toute les ressources dispo
    private void threads(){
        Thread thread;

        for(FilmImg film : this.filmList) {
            // thread = new Thread(new MyRunnable(film, this.adapter, this.sImgURL, this.handlerUI));
            // On va utiliser l'URL fournit dans la fiche film pour télécahrger la couverture
            thread = new Thread(new MyRunnable( film, this.adapter,
                                                film.getFilm().getPoster(), this.handlerUI));

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
        for(FilmImg film : this.filmList) {
            // this.handlerFilm.post(new MyRunnable(film, this.adapter, this.sImgURL, this.handlerUI));
            // On va utiliser l'URL fournit dans la fiche film pour télécahrger la couverture
            this.handlerFilm.post(new MyRunnable(film, this.adapter,
                                                 film.getFilm().getPoster(), this.handlerUI));
        }
    }

    // Un Handler Thread Message fonctionne comme un Handler Thread classique
    // Il permet, en plus, le post de Message qui sont des évenement
    // Le handler devra donc pouvoir réagir en fonction du type de message
    // On peut passer des objets directement dans le message
    private void handlerTM(){
        for(FilmImg film : this.filmList) {
            // Au lieu de recréer un message à chaque film
            // on va obtenir un message déjà existant si possible
            Message msg = Message.obtain();
            // Donne le type depuis l'énumération de la casse du type de message
            msg.what = MyHandlerThreadMessage.iMsgType_DownloadImg;
            // Lui passe un objet en paramètre, devoir faire un objet pour englober tout
            // msg.obj = new ParamThread(film, this.adapter, this.sImgURL, this.handlerUI);
            // On va utiliser l'URL fournit dans la fiche film pour télécahrger la couverture
            msg.obj = new ParamThread(film, this.adapter, film.getFilm().getPoster(), this.handlerUI);

            // Post un message dans la queue du handler sous forme runnable
            this.handlerFilmMessage.sendMessage(msg);
        }
    }

    // On veut maitriser le nombre de thread simultanné exécuté dans un pool
    // Même si on met 4 en nombre de pool initial, s'il y a un coeur du CPU qui est en veille
    // L'on aura pas les 4 pool initial
    private void pool(){
        for(FilmImg film : this.filmList) {
            //this.threadPoolExecutor.execute(new MyRunnable(film, this.adapter,this.sImgURL, this.handlerUI));
            // On va utiliser l'URL fournit dans la fiche film pour télécahrger la couverture
            this.threadPoolExecutor.execute(new MyRunnable(film, this.adapter,
                                                    film.getFilm().getPoster(), this.handlerUI));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_ajout:
                this.checkLocationPermission();
                this.showDialog();
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

    // Quand l'utilisateur répond à la requête de permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilmListeActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this.getApplicationContext(),"Permission accordée",
                                    Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this.getApplicationContext(),"Permission non accordée",
                            Toast.LENGTH_SHORT).show();
                    // permission not granted
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            case FilmListeActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this.getApplicationContext(),"Permission accordée",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this.getApplicationContext(),"Permission non accordée",
                            Toast.LENGTH_SHORT).show();
                    // permission not granted
                    startActivity(new Intent(this, FilmListeActivity.class));
                    finish();
                }
                break;
            // other 'case' lines to check for other
            // permissions this app might request.
            default:
                break;
        }

    }

    public void checkLocationPermission(){
        // Gestion des permissions
        // NE SURTOUT PAS OUBLIER DE LES DECLARER DANS LE MANIFEST
        // Attention ne fonctionne pas en version API 5 ou moins
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Demande à l'utilisateur la permission si pas déjà donné
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void showDialog(){
        // Recupère le donneur de vie de layout de l'activité
        LayoutInflater li = LayoutInflater.from(this.context);
        // Récupère la vue du layout du dialogue
        View promptsView = li.inflate(R.layout.dialog_id, null);
        // Créer une alerte dialogue
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);

        // Set le layout.xml à la vue to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // Récupération en constante du mot de passe entré par l'utilisateur
        final EditText idInput = promptsView.findViewById(R.id.txt_id);

        // set dialog message
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to property of the class
                                String idFilm = idInput.getText().toString();
                                FilmListeActivity.this.filmID = idFilm;
                                Toast.makeText(getApplicationContext(),idFilm,
                                        Toast.LENGTH_SHORT).show();
                                FilmListeActivity.this.ajoutFilm(idFilm);
                            }
                        })
                .setNegativeButton("Annuler",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                Intent intent = new Intent( getApplicationContext(),
                                                            FilmListeActivity.class);
                                startActivity(intent);
                                // When calling finish() on an activity, the method onDestroy()
                                // is executed this method can do things like:
                                // Dismiss any dialogs the activity was managing.
                                // Close any cursors the activity was managing.
                                // Close any open search dialog
                                // Permet d'enlever l'activité du dessus
                                finish();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
