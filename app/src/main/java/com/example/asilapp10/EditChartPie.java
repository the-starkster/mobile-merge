package com.example.asilapp10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity per l'aggiunta di dati al grafico a torta.
 */
public class EditChartPie extends AppCompatActivity {

    EditText editTextFood, editTextMedicines, editTextOther;
    FirebaseUser user;
    Button buttonBack, buttonAdd;
    FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chart_pie);

        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Inizializzazione dei componenti UI e degli oggetti Firebase

        buttonBack = findViewById(R.id.btn_add_back);
        buttonAdd = findViewById(R.id.btn_add_add);

        editTextFood = findViewById(R.id.edit_text_food);
        editTextMedicines = findViewById(R.id.edit_text_medicines);
        editTextOther = findViewById(R.id.edit_text_other);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Gestisce il click sul pulsante "Indietro"

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChartPie.class);
            startActivities(new Intent[]{intent});
            finish();
        });

        // Ottiene i valori inseriti dall'utente per cibo, medicine e altro

        buttonAdd.setOnClickListener(v -> {
            double[] food = { editTextFood.getText().toString().isEmpty() ? 0.0
                    : Double.parseDouble(editTextFood.getText().toString()) };
            double[] medicines = { editTextMedicines.getText().toString().isEmpty() ? 0.0
                    : Double.parseDouble(editTextMedicines.getText().toString()) };
            double[] other = { editTextOther.getText().toString().isEmpty() ? 0.0
                    : Double.parseDouble(editTextOther.getText().toString()) };

            String userId = user.getUid();
            DocumentReference docRef = db.collection("Chart Pie Data")
                    .document(userId + " FMO");

            // Ottiene i dati esistenti dal documento su Firestore

                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Double> values = new ArrayList<>();

                        Object rawData = documentSnapshot.get(formattedDate);
                        if (rawData instanceof List<?>) {
                            List<?> rawList = (List<?>) rawData;

                            // Estrae i valori esistenti dal documento
                            //Controllo se sono delle istanze di Double e Integer

                            for (Object item : rawList) {
                                if (item instanceof Double) {
                                    values.add((Double) item);
                                } else if (item instanceof Integer) {
                                    values.add(((Integer) item).doubleValue());
                                }
                            }
                        }

                        Double foodValue = 0.0;
                        Double medicinesValue = 0.0;
                        Double otherValue = 0.0;

                        // Se ci sono almeno 3 valori, assegna i valori alle categorie

                        if (values.size() >= 3) {
                            foodValue = values.get(0);
                            medicinesValue = values.get(1);
                            otherValue = values.get(2);
                        }

                        if (foodValue != null && medicinesValue != null && otherValue != null) {

                            // Aggiorna i valori con quelli inseriti dall'utente

                            food[0] += foodValue;
                            medicines[0] += medicinesValue;
                            other[0] += otherValue;

                            // Crea una lista con i valori aggiornati

                            List<Double> updatedValues = Arrays.asList(food[0], medicines[0], other[0]);

                            // Aggiorna il documento su Firestore con i nuovi valori

                            docRef.update(formattedDate, updatedValues)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(EditChartPie.this, "Data saved!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(EditChartPie.this, "Error!", Toast.LENGTH_SHORT).show());

                            // Torna alla schermata del grafico a torta

                            Intent intent = new Intent(getApplicationContext(), ChartPie.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(e -> {
                    // Gestisce eventuali errori
                });
        });
    }
}