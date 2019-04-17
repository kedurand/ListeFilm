package com.example.listefilm.handler;

import android.os.Handler;
import android.os.Message;

import com.example.listefilm.model.ParamThread;
import com.example.listefilm.thread.MyRunnable;


// Il faut implémenter le callback sur message en Handler
public class MyHandlerThreadMessage implements Handler.Callback {
    //Constante public de classe pour faire une énumeration
    public static final int iMsgType_DownloadImg = 1;

    // Effectue le traitement selon le type de message envoyé
    @Override
    public boolean handleMessage(Message message) {
        boolean retour = false;
        System.out.println(message.what);
        switch(message.what){
            case MyHandlerThreadMessage.iMsgType_DownloadImg:
                ParamThread paramThread = (ParamThread) message.obj;
                // Utilisation du Runnable déjà codé pour éviter de recoder le run()
                // Le runnable contient la weakreference qui va bien
                MyRunnable myRunnable = new MyRunnable( paramThread.getFilmImg(),
                                                        paramThread.getAdapter(),
                                                        paramThread.getUrl(),
                                                        paramThread.getHandlerUI());
                // Nous sommes déjà dans un thread (message) envoyé dans un Handler
                // On ne donc pas encore faire un thread à l'intérieur pour le gérer
                myRunnable.run();
                retour = true;
                break;
            default:
                break;
        }
        return retour;
    }

}
