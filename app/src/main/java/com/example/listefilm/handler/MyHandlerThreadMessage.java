package com.example.listefilm.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.example.listefilm.FilmListeActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Obligatoire d'étendre la classe HandlerThread pour implémenter le callback sur message
public class MyHandlerThreadMessage extends HandlerThread implements Handler.Callback {
    //Constante public de classe pour faire une énumeration
    public static final int iMsgType_DownloadImg = 1;

    public MyHandlerThreadMessage(String name) {
        super(name);
    }

    public MyHandlerThreadMessage(String name, int priority) {
        super(name, priority);
    }

    // Effectue le traitement selon le type de message envoyé
    @Override
    public boolean handleMessage(Message message) {
//        switch(message.what){
//            case MyHandlerThreadMessage.iMsgType_DownloadImg:
//                try {
//                    Thread.sleep(1000);
//                    Bitmap btm = this.downloadBitmapFromURL(this.url);
//                    message.getData();
//                    this.film.setImage(btm);
//                    this.handlerUI.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                break;
//            default:
//                break;
//        }
//
        return false;
    }

    private Bitmap downloadBitmapFromURL(String url) {
        URL monURL = null;
        HttpURLConnection urlConnection = null;
        Bitmap bmp = null;

        try {
            monURL = new URL(url);
            urlConnection = (HttpURLConnection) monURL.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bmp = BitmapFactory.decodeStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bmp;
    }
}
