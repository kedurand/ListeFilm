package com.example.listefilm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.listefilm.adapter.CertificateAdapter;
import com.example.listefilm.model.Certificate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class CertificateListeActivity extends AppCompatActivity implements View.OnClickListener {
    private final Context context = this;

    private EditText etHTTPS;
    private Button   btAjout;
    private Button   btCharger;
    private Button   btSauvegarder;
    private ListView listView;
    private List<Certificate> certificateList;
    private CertificateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificateliste);

        this.initialize();
        this.loadingListView();
        this.showDialog();
    }

    private void initialize() {
        this.etHTTPS = findViewById(R.id.et_https);
        this.btAjout = findViewById(R.id.bt_ajout);
        this.btAjout.setOnClickListener(this);
        this.btCharger = findViewById(R.id.bt_charger);
        this.btCharger.setOnClickListener(this);
        this.btSauvegarder = findViewById(R.id.bt_sauvegarder);
        this.btSauvegarder.setOnClickListener(this);
        this.listView = findViewById(R.id.lv_certificateList);
    }

    private void loadingListView() {
        this.certificateList = new ArrayList<>();
        this.certificateList.addAll(Certificate.listAll(Certificate.class));

        this.adapter = new CertificateAdapter(this.getApplicationContext(), this.certificateList);

        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new ListClickHandler());
    }

    private void ajout(){
        Toast.makeText(this.getApplicationContext(),"Bouton Ajout",
                Toast.LENGTH_SHORT).show();
    }

    private void charger(){
        Toast.makeText(this.getApplicationContext(),"Bouton Charger",
                Toast.LENGTH_SHORT).show();
    }

    private void sauvegarder(){
        Toast.makeText(this.getApplicationContext(),"Bouton Sauvegarder",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_ajout:
                this.ajout();
                break;
            case R.id.bt_charger:
                this.charger();
                break;
            case R.id.bt_sauvegarder:
                this.sauvegarder();
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

    private void showDialog(){
        // Recupère le donneur de vie de layout de l'activité
        LayoutInflater li = LayoutInflater.from(this.context);
        // Récupère la vue du layout du dialogue
        View promptsView = li.inflate(R.layout.dialog_password, null);
        // Créer une alerte dialogue
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);

        // Set le layout.xml à la vue to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // Récupération en constante du mot de passe entré par l'utilisateur
        final EditText pwdInput = promptsView.findViewById(R.id.pwd_password);

        // set dialog message
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // get user input and set it to result
                            // edit text
                            Toast.makeText(getApplicationContext(),pwdInput.getText(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                .setNegativeButton("Annuler",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        Intent intent = new Intent( getApplicationContext(),
                                                    MainActivity.class);
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

    private void getCertificateFromHTTPS(String url){
        URL monURL = null;
        HttpsURLConnection urlConnection = null;

        try{
            monURL = new URL(url);
            urlConnection = (HttpsURLConnection) monURL.openConnection();
            //TODO: récuéprer le certificat sous quel format ?
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
    }
}
