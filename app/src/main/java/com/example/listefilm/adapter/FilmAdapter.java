package com.example.listefilm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.listefilm.R;
import com.example.listefilm.model.FilmImg;

import java.util.List;

// Adaptater entre les données et la liste view du layout
public class FilmAdapter extends ArrayAdapter<FilmImg>{
    private Context         context;
    private List<FilmImg>  filmList;

    public FilmAdapter(Context context, List<FilmImg> filmList) {
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
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        FilmHolder holder;

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.cell_filmliste,
                    viewGroup, false);

            holder = new FilmHolder();
            holder.image        = view.findViewById(R.id.img_affiche);
            holder.titre        = view.findViewById(R.id.txt_titre);
            holder.realisateur  = view.findViewById(R.id.txt_realisateur);
            holder.producteur   = view.findViewById(R.id.txt_producteur);
            holder.annee        = view.findViewById(R.id.txt_annee);

            view.setTag(holder);
        }
        else{
            holder = (FilmHolder) view.getTag();
        }

        holder.image.setImageBitmap(this.filmList.get(i).getImg());
        holder.titre.setText(this.filmList.get(i).getFilm().getTitle());
        holder.realisateur.setText(this.filmList.get(i).getFilm().getDirector());
        holder.producteur.setText(this.filmList.get(i).getFilm().getProduction());
        holder.annee.setText(this.filmList.get(i).getFilm().getYear());

        return view;
    }
}
