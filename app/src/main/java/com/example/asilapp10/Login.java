package com.example.asilapp10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();

        // Verifica se un utente è già autenticato all'avvio dell'app

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            // Se c'è già un utente autenticato, reindirizza a MainActivity

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inizializza l'istanza di FirebaseAuth

        mAuth = FirebaseAuth.getInstance();

        // Inizializza le view e i pulsanti

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        // Gestisce il click sul link "Register Now"

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        // Gestisce il click sul pulsante di login

        buttonLogin.setOnClickListener(v -> {
            String email = "";
            String password = "";

            // Ottiene l'email e la password inserite dall'utente
            if (editTextEmail.getText() != null) {
                email = editTextEmail.getText().toString();
            }

            if (editTextPassword.getText() != null) {
                password = editTextPassword.getText().toString();
            }

            // Controlla se l'email e la password sono vuote
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return; // Interrompe l'esecuzione ulteriore se manca email o password
            }

            progressBar.setVisibility(View.VISIBLE);

            // Effettua il login tramite Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Successo del login
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Fallimento del login
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}