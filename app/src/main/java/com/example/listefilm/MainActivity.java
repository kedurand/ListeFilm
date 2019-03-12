package com.example.listefilm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btGoToFilmListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();
    }

    private void initialize(){
        //Enregistrer le bouton pour le listener
        this.btGoToFilmListe = (Button) findViewById(R.id.bt_consulterListe);
        this.btGoToFilmListe.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch(view.getId()){
            case R.id.bt_consulterListe:
                intent = new Intent(this, FilmListeActivity.class);
                break;
            default:
                break;
        }

        startActivity(intent);
    }
}
