package com.example.asilapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.asilapp10.maps.MapsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    FirebaseAuth auth;
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

        textSend.setOnClickListener(v -> {
            ratingBar.setVisibility(View.GONE);
            textRating.setVisibility(View.GONE);
            textSend.setVisibility(View.GONE);
            textThankYou.setVisibility(View.VISIBLE);
        });

        //BottomNavigationMenu Setup, set listener, select "home" by default
        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigationMenu);
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    FragmentHome homeFragment = new FragmentHome();
    MapsFragment mapsFragment = new MapsFragment();
    FragmentUserData userDataFragment = new FragmentUserData();
    FragmentHealthProfile healthProfileFragment = new FragmentHealthProfile();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.navigation_home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        else if (id == R.id.navigation_maps) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mapsFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        else if (id == R.id.navigation_pie_chart) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChartPieFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();

            return true;
        }
        else if (id == R.id.navigation_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, userDataFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        else if (id == R.id.navigation_health_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, healthProfileFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return false;
    }
}