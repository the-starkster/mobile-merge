package com.example.asilapp10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarPieChart extends AppCompatActivity {
    DatePicker datePickerStart;
    DatePicker datePickerEnd;
    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth mAuth;
    CollectionReference collectionRef;
    Button button;
    String startDate = ""; // Variabile per la data di inizio
    String endDate = "";   // Variabile per la data di fine
    int totalFood = 0;
    int totalMedicines = 0;
    int totalOther = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_pie_chart);

        button = findViewById(R.id.btn_apply);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String userId = user.getUid();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChartPie.class);
                intent.putExtra("totalFood", totalFood);
                intent.putExtra("totalMedicines", totalMedicines);
                intent.putExtra("totalOther", totalOther);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();



        // Imposta un listener per rilevare il cambio di data
        datePickerStart.init(datePickerStart.getYear(), datePickerStart.getMonth(), datePickerStart.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Esegui azioni con la data selezionata
                        // year, monthOfYear e dayOfMonth rappresentano la data selezionata
                        String selectedDatePie = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        startDate = selectedDatePie;
                    }
                });

        datePickerEnd.init(datePickerEnd.getYear(), datePickerEnd.getMonth(), datePickerEnd.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Esegui azioni con la data selezionata
                        // year, monthOfYear e dayOfMonth rappresentano la data selezionata
                        String selectedDatePie = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        endDate = selectedDatePie;
                    }
                });

        collectionRef = db.collection("Char Pie Data")
                .document(userId + " Charges")
                .collection("Dates");

        Log.d("Valore Food", "Valore: " + startDate);

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentName = document.getId(); // Ottieni il nome del documento

                        // Verifica se il nome del documento rientra nel range di date desiderato
                        if (isDateInRange(documentName, "2023-12-22", "2023-12-23")) {
                            // Il nome del documento rientra nel range di date, puoi ottenere i dati del documento
                            Map<String, Object> data = document.getData();

                            // Esegui le operazioni necessarie con i dati ottenuti
                            Integer food = (Integer) data.get("Food");
                            totalFood += (food != null) ? food : 0;
                            Log.d("Valoreer Food", "Valore: " + totalFood);

                            Integer medicines = (Integer) data.get("Medicines");
                            totalMedicines += (medicines != null) ? medicines : 0;
                            Log.d("Valore Food", "Valore: " + totalFood);

                            Integer other = (Integer) data.get("Other");
                            totalOther += (other != null) ? other : 0;
                            Log.d("Valore Food", "Valore: " + totalFood);
                        }
                    }
                } else {
                    Log.d("Valoreer Food", "Valore: " + totalFood);
                }
            }
        });
    }

    // Funzione per verificare se una data è nel range specificato
    private boolean isDateInRange(String date, String startDate, String endDate) {
        // Confronta le stringhe delle date nel formato "yyyy-MM-dd"
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0;
    }
}