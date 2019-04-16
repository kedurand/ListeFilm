package com.example.listefilm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


// https://github.com/nassimkhatir?tab=repositories
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btGoToFilmListe;
    Button btGoToCertificateListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();
    }

    private void initialize(){
        //Enregistrer le bouton pour le listener
        this.btGoToFilmListe = findViewById(R.id.bt_consulterListeFilm);
        this.btGoToFilmListe.setOnClickListener(this);

        this.btGoToCertificateListe = findViewById(R.id.bt_consulterListeCertificate);
        this.btGoToCertificateListe.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch(view.getId()){
            case R.id.bt_consulterListeFilm:
                intent = new Intent(this, FilmListeActivity.class);
                break;
            case R.id.bt_consulterListeCertificate :
                intent = new Intent(this, CertificateListeActivity.class);
                break;
            default:
                break;
        }

        startActivity(intent);
    }
}
