package com.example.listefilm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listefilm.adapter.CertificateAdapter;
import com.example.listefilm.model.MonCertificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class CertificateListeActivity extends AppCompatActivity implements View.OnClickListener {
    private final Context context = this;

    private KeyStore keyStore;
    private SSLContext sslContext;

    private EditText etHTTPS;
    private Button   btAjout;
    private Button   btCheck;
    private TextView txtResult;
    private ListView listView;
    private List<MonCertificate> certificateList;
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
        // Ici, c'est une mauvaise pratique, par défault, dans le thread UI (le principal)
        // On ne peut pas faire de connection internet car elles peuvent être bloquante
        // Et faire freeze l'application, on doit donc passer par une async task pour les connexion
        // Ne pas oublier d'autoriser l'application à aller sur Internet dans le manifest
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            // KeyStore pour stocker les certificats
            this.keyStore = KeyStore.getInstance("PKCS12");
            // Il faut créer le keystore
            this.keyStore.load(null, null);
            // On ignore tout ce qui est stockage dans un fichier ici
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        catch (CertificateException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.etHTTPS = findViewById(R.id.et_https);
        this.btAjout = findViewById(R.id.bt_ajout);
        this.btAjout.setOnClickListener(this);
        this.btCheck = findViewById(R.id.bt_check);
        this.btCheck.setOnClickListener(this);
        this.txtResult = findViewById(R.id.txt_result);
        this.listView = findViewById(R.id.lv_certificateList);
    }

    private void loadingListView() {
        this.certificateList = new ArrayList<>();
        this.certificateList.addAll(MonCertificate.listAll(MonCertificate.class));

        this.adapter = new CertificateAdapter(this.getApplicationContext(), this.certificateList);

        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new ListClickHandler());
    }

    // Recharge la List avec ce qui est dans la BD
    private void refreshListView(){
        this.certificateList.clear();
        this.certificateList.addAll(MonCertificate.listAll(MonCertificate.class));
        this.adapter.notifyDataSetChanged();
    }

    private void ajout(String url){
        URL monURL;
        HttpsURLConnection urlConnection = null;

        try{
            monURL = new URL(url);
            urlConnection = (HttpsURLConnection) monURL.openConnection();
            // Il faut se connecter dans tous les cas, l'open ne fait que la préparer
            urlConnection.connect();
            // Récupération de la liste des certificats dont celle de l'autorité
            Certificate[] cs = urlConnection.getServerCertificates();

            for (Certificate c : cs){
                // Caster en ce type de certificat pour récupérer certaines informations
                // Certificate est une classe abstraite
                X509Certificate xcs = (X509Certificate) c;
                // IssuerDN, ID du certficat
                this.keyStore.setCertificateEntry(xcs.getIssuerDN().getName(), c);

                // Chargement du sujet de chaque certificat dans notre listView
                MonCertificate mc = new MonCertificate(xcs.getSubjectDN().getName());

                // Sauvegarde le certificat dans la BDD
                // mc.save(); //Faudrait lier le keystroke à cette liste !!
                mc.save();

                this.certificateList.add(mc);
            }
            //Refresh de la liste d'après la BDD
            // this.refreshListView();
            this.adapter.notifyDataSetChanged();

            Toast.makeText(this.getApplicationContext(),"Ajout des certificats réussi !",
                    Toast.LENGTH_SHORT).show();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void check(String url){
        String tmfA;
        TrustManagerFactory tmf;
        URL monURL;
        HttpsURLConnection urlConnection = null;

        // Il faut initialiser notre contexte de sécurité
        // Pour ce faire, il nous faut un TrustManager init avec le keystore
        try {
            tmfA = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfA);
            tmf.init(this.keyStore);

            this.sslContext = SSLContext.getInstance("TLS");
            this.sslContext.init(null, tmf.getTrustManagers(), null);

            // Connection à l'URL
            monURL =  new URL(url);
            urlConnection = (HttpsURLConnection) monURL.openConnection();
            // Il faut set l'environnement de sécurité de connexion, en utiliser nos certificats
            // stocké dans le keystroke via le trustmanager
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            // On se connecte à l'URL grâce à nos certificats
            urlConnection.connect();

            // Connexion réussie
            Toast.makeText(this.getApplicationContext(),"Certificates Verified !",
                    Toast.LENGTH_SHORT).show();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (KeyStoreException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
            // Connexion réussie
            Toast.makeText(this.getApplicationContext(),"Certificates NOT Verified !",
                    Toast.LENGTH_SHORT).show();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_ajout:
                this.ajout(this.etHTTPS.getText().toString());
                break;
            case R.id.bt_check:
                this.check(this.etHTTPS.getText().toString());
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

                                // Encrypte les certificats
                                CertificateListeActivity.this.encryptFile(pwdInput.getText().toString());

                                try {
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                // Decrypte les certificats
                                CertificateListeActivity.this.decryptFile(pwdInput.getText().toString());
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


    public void encryptFile(String pwd){
        try {
            // Récupère le mot de passe sous forme de tableau de byte
            byte[] pwdByte = pwd.getBytes();

            // Permet d'effectuer du hachage
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            // Récupère les données
            messageDigest.update(pwdByte);
            // Digère et remplie les données puis réinitialise le tout
            byte[] digest = messageDigest.digest();
            // Redimenssionne le hashage en 16
            digest = Arrays.copyOf(digest, 16);

            // Représente le secret
            SecretKey secretKeyAES = new SecretKeySpec(digest, "AES256");

            // Utilisation d'un CS symétrique, utilisation d'un algo plus que AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // Association du secret crée plus haut au CS cipher
            cipher.init(Cipher.ENCRYPT_MODE, secretKeyAES);

            // Le path de l'application courante
            String applicationPath = this.getApplicationContext().getFilesDir().getPath();
            // Création du fichier à encrypter
            File encryptedFile = new File(applicationPath + File.separator + "pwdEncrypted.slr");
            // Création d'une instance pour écrire sur ce fichier
            FileOutputStream fos = new FileOutputStream(encryptedFile);

            // Application du CS symétrique cipher crée plus haut
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Création d'une abstraction Object pour y insérer des objets
            // Ces objets doivent impérative implémenter la classe Serializable
            ObjectOutputStream oos = new ObjectOutputStream(cos);
            // Normalement on devrait y écrire des objets
            oos.writeObject(this.certificateList);
            // On force l'écriture dans le fichier
            oos.flush();
            oos.close();

            Toast.makeText(this.context, "Encryption du fichier est un succés !",
                    Toast.LENGTH_SHORT).show();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void decryptFile(String pwd){
        try {
            // On veut récupérer la liste des certificats présent dans le fichier encrypté
            List<MonCertificate> listCertificates = new ArrayList<>();

            // Récupère le mot de passe sous forme de tableau de byte
            byte[] pwdByte = pwd.getBytes();

            // Permet d'effectuer du hachage
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            // Récupère les données
            messageDigest.update(pwdByte);
            // Digère et remplie les données puis réinitialise le tout
            byte[] digest = messageDigest.digest();
            // Redimenssionne le hashage en 16
            digest = Arrays.copyOf(digest, 16);

            // Représente le secret
            SecretKey secretKeyAES = new SecretKeySpec(digest, "AES256");

            // Utilisation d'un CS symétrique, utilisation d'un algo plus que AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // Association du secret crée plus haut au CS cipher
            cipher.init(Cipher.DECRYPT_MODE, secretKeyAES);

            // Le path de l'application courante
            String applicationPath = this.getApplicationContext().getFilesDir().getPath();
            // Création du fichier à encrypter
            File encryptedFile = new File(applicationPath + File.separator + "pwdEncrypted.slr");
            // Création d'une instance pour lire sur ce fichier
            FileInputStream fis = new FileInputStream(encryptedFile);

            // Application du CS symétrique cipher crée plus haut
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            // Création d'une abstraction Object pour y insérer des objets
            ObjectInputStream ois = new ObjectInputStream(cis);
            // Récupération des objects présent dans le fichier
            listCertificates = (List<MonCertificate>) ois.readObject();
            // On force l'écriture dans le fichier
            ois.close();

            Toast.makeText(this.context, "Décryption du fichier est un succés !",
                    Toast.LENGTH_SHORT).show();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
