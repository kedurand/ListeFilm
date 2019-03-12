package com.example.listefilm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.listefilm.R;
import com.example.listefilm.model.Film;

import java.util.List;

// Adaptater entre les données et la liste view du layout
public class FilmAdapter extends ArrayAdapter<Film>{
    private Context     context;
    public  List<Film>  filmList;

    public FilmAdapter(Context context, List<Film> filmList) {
        super(context, R.layout.activity_filmliste, filmList);
        this.context = context;
        this.filmList = filmList;
    }

    public class FilmHolder{
        ImageView   image;
        TextView    titre;
        TextView    realisateur;
        TextView    producteur;
        TextView    annee;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FilmHolder holder;

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.cell_filmliste,
                    viewGroup, false);

            holder = new FilmHolder();
            holder.image        = (ImageView)   view.findViewById(R.id.img_affiche);
            holder.titre        = (TextView)    view.findViewById(R.id.txt_titre);
            holder.realisateur  = (TextView)    view.findViewById(R.id.txt_realisateur);
            holder.producteur   = (TextView)    view.findViewById(R.id.txt_producteur);
            holder.annee        = (TextView)    view.findViewById(R.id.txt_annee);

            view.setTag(holder);
        }
        else{
            holder = (FilmHolder) view.getTag();
        }

        holder.image.setImageBitmap(this.filmList.get(i).getImage());
        holder.titre.setText(this.filmList.get(i).getTitre());
        holder.realisateur.setText(this.filmList.get(i).getRealisateur());
        holder.producteur.setText(this.filmList.get(i).getProducteur());
        holder.annee.setText(this.filmList.get(i).getAnnee());

        return view;
    }
}
