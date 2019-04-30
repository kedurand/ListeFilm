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
import com.example.listefilm.model.SearchImg;

import java.util.List;

// Adaptater entre les données et la liste view du layout
public class SearchAdapter extends ArrayAdapter<SearchImg>{
    private Context          context;
    private List<SearchImg>  searchList;

    public SearchAdapter(Context context, List<SearchImg> searchList) {
        super(context, R.layout.dialog_searchliste, searchList);
        this.context = context;
        this.searchList = searchList;
    }

    public class FilmHolder{
        ImageView   image;
        TextView    titre;
        TextView    annee;
        TextView    type;
        TextView    imdbID;
    }

    @NonNull
    @Override
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        FilmHolder holder;

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.cell_searchliste,
                    viewGroup, false);

            holder = new FilmHolder();
            holder.image    = view.findViewById(R.id.img_affiche);
            holder.titre    = view.findViewById(R.id.txt_titre);
            holder.annee    = view.findViewById(R.id.txt_annee);
            holder.type     = view.findViewById(R.id.txt_type);
            holder.imdbID   = view.findViewById(R.id.txt_id);

            view.setTag(holder);
        }
        else{
            holder = (FilmHolder) view.getTag();
        }

        holder.image.setImageBitmap(this.searchList.get(i).getImg());
        holder.titre.setText(this.searchList.get(i).getSearch().getTitle());
        holder.annee.setText(this.searchList.get(i).getSearch().getYear());
        holder.type.setText(this.searchList.get(i).getSearch().getType());
        holder.imdbID.setText(this.searchList.get(i).getSearch().getImdbID());

        return view;
    }
}
