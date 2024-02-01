package com.example.asilapp10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.asilapp10.QRCode.QRCodeFragment;
import com.example.asilapp10.maps.MapsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button buttonFragmentQR, buttonChartPie, buttonHome,buttonOther;
    TextView textRating, textSend, textThankYou;
    RatingBar ratingBar;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializza l'istanza di FirebaseAuth

        auth = FirebaseAuth.getInstance();

        // Trova i pulsanti nel layout

        buttonFragmentQR = findViewById(R.id.btn_qr);
        buttonChartPie = findViewById(R.id.btn_pie);
        buttonHome = findViewById(R.id.btn_home);
        buttonOther = findViewById(R.id.btn_other_main);

        textRating = findViewById(R.id.btn_review);
        textSend = findViewById(R.id.t_send);
        textThankYou = findViewById(R.id.thank_you);

        ratingBar = findViewById(R.id.ratingBar);

        // Ottieni l'utente corrente

        user = auth.getCurrentUser();

        // Verifica se l'utente è loggato, altrimenti reindirizza alla schermata di login

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivities(new Intent[]{intent});
            finish();
        }



        // Configura il click listener per il pulsante del codice QR
        buttonFragmentQR.setOnClickListener(v -> {

            // Ottieni il gestore dei frammenti

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Crea e sostituisci il frammento dell'utente

            QRCodeFragment qrcodeFragment = new QRCodeFragment();
            fragmentTransaction.replace(R.id.fragment_container, qrcodeFragment);
            fragmentTransaction.commit();
        });




        //Configura il click listener per il pulsante del grafico a torta
        buttonChartPie.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChartPie.class);
            startActivity(intent);
            finish();
        });

        //Configura il click listener per il pulsante home
        buttonHome.setOnClickListener(v ->{

            // Ottieni il gestore dei frammenti

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Crea e sostituisci il frammento dell'utente

            FragmentRules rules = new FragmentRules();
            fragmentTransaction.replace(R.id.fragment_container, rules);
            fragmentTransaction.commit();
        });

        //Configura il click listener per far visualizzare il video all'utente



        //Configura il click listener per far apparire la valutazione
        textRating.setOnClickListener(v ->{
            textSend.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
        } );

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (fromUser) {
                Log.d("RatingBar", "Nuova valutazione: " + rating);
                // Questo codice viene eseguito ogni volta che l'utente cambia la valutazione
                // Aggiungi qui la tua logica, ad esempio per salvare la valutazione
            }
        });

        buttonOther.setOnClickListener(v ->{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Crea e sostituisci il frammento dell'utente

            FragmentOtherView other = new FragmentOtherView();
            fragmentTransaction.replace(R.id.fragment_container, other);
            fragmentTransaction.commit();
        });

        textSend.setOnClickListener(v -> {
            ratingBar.setVisibility(View.GONE);
            textRating.setVisibility(View.GONE);
            textSend.setVisibility(View.GONE);
            textThankYou.setVisibility(View.VISIBLE);
        });
    }
}