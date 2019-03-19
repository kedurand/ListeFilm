package com.example.listefilm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.listefilm.R;
import com.example.listefilm.model.Certificate;

import java.util.List;

// Adaptater entre les données et la liste view du layout
public class CertificateAdapter extends ArrayAdapter<Certificate>{
    private Context             context;
    public  List<Certificate>   certificateList;

    public CertificateAdapter(Context context, List<Certificate> certificateList) {
        super(context, R.layout.activity_certificateliste, certificateList);
        this.context = context;
        this.certificateList = certificateList;
    }

    public class CertificateHolder{
        TextView contenu;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CertificateHolder holder;

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.cell_certificateliste,
                    viewGroup, false);

            holder = new CertificateHolder();
            holder.contenu = view.findViewById(R.id.txt_contenu);

            view.setTag(holder);
        }
        else{
            holder = (CertificateHolder) view.getTag();
        }

        holder.contenu.setText(this.certificateList.get(i).getContenu());

        return view;
    }
}
