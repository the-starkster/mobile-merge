package com.example.asilapp10;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChartPieFragment extends Fragment {

    AnyChartView anyChartView; // Vista per il grafico a torta
    DatePicker datePickerStart; // DatePicker per la data di inizio
    DatePicker datePickerEnd; // DatePicker per la data di fine
    String startDate = ""; // Data di inizio selezionata
    String endDate = ""; // Data di fine selezionata
    Button edit, apply; // Pulsanti "Edit" e "Apply"
    String[] categories = {"Food", "Medicines", "Other"}; // Categorie per il grafico a torta
    private Pie pie; // Oggetto Pie del grafico
    FirebaseUser user; // Utente autenticato
    FirebaseAuth mAuth; // Oggetto per l'autenticazione
    String userId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart_pie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottiene la data corrente




        anyChartView = view.findViewById(R.id.any_chart_view); // Inizializza la vista del grafico
        edit = view.findViewById(R.id.b_edit_chart_pie); // Inizializza il pulsante "Edit"

        apply = view.findViewById(R.id.btn_apply_test); // Inizializza il pulsante "Apply"

        datePickerStart = view.findViewById(R.id.d_pie_start); // Inizializza il DatePicker di inizio
        datePickerEnd = view.findViewById(R.id.d_pie_end); // Inizializza il DatePicker di fine

        mAuth = FirebaseAuth.getInstance(); // Ottiene l'istanza di FirebaseAuth
        user = mAuth.getCurrentUser(); // Ottiene l'utente corrente

        userId = (user != null) ? user.getUid() : "";



        // Ottiene i dati del grafico a torta dal database
        loadPieChart();



        // Listener per il pulsante "Edit"

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditChartPie.class);
            startActivity(intent);
        });

        // Listener per il pulsante "Apply"

        apply.setOnClickListener(aView -> {

            // Ottiene la data di inizio e la data di fine selezionate dall'utente

            startDate = getCurrentFormattedDate(datePickerStart.getYear(), datePickerStart.getMonth(), datePickerStart.getDayOfMonth());
            endDate = getCurrentFormattedDate(datePickerEnd.getYear(), datePickerEnd.getMonth(), datePickerEnd.getDayOfMonth());

            // Imposta i listener per i datepicker per aggiornare le date selezionate

            datePickerStart.init(datePickerStart.getYear(), datePickerStart.getMonth(), datePickerStart.getDayOfMonth(),
                    (v, year, monthOfYear, dayOfMonth) ->
                            startDate = getCurrentFormattedDate(year, monthOfYear, dayOfMonth));

            datePickerEnd.init(datePickerEnd.getYear(), datePickerEnd.getMonth(), datePickerEnd.getDayOfMonth(),
                    (v, year, monthOfYear, dayOfMonth) ->
                            endDate = getCurrentFormattedDate(year, monthOfYear, dayOfMonth));

            // Esegue una query su Firestore per ottenere i dati

            performFirestoreQuery(userId);
        });
    }


    /**
     * Configura e visualizza un grafico a torta (pie chart) con i valori forniti.
     *
     * @param valuesArray Un array di valori da visualizzare nel grafico a torta.
     */
    public void setupPieChart(double[] valuesArray) {
        // Verifica se il grafico a torta non è stato inizializzato

        if (pie == null) {

            // Inizializza il grafico a torta e lo imposta nella vista anyChartView

            pie = AnyChart.pie();
            anyChartView.setChart(pie);
        }

        // Crea una lista di data entries per il grafico a torta

        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            dataEntries.add(new ValueDataEntry(categories[i], valuesArray[i]));
        }

        // Disabilita il ridisegno automatico del grafico a torta

        pie.autoRedraw(false);

        // Imposta i dati nel grafico a torta

        pie.data(dataEntries);

        // Imposta la palette dei colori per le fette del grafico a torta

        pie.palette(new String[]{"#ED2B2B", "#53C943", "#2D70D3"});

        // Abilita nuovamente il ridisegno automatico del grafico a torta

        pie.autoRedraw(true);
    }


    /**
     * Esegue una query su Firestore per ottenere dati per il grafico a torta in base alle date specificate.
     *
     * @param userId L'ID dell'utente per cui eseguire la query.
     */
    private void performFirestoreQuery(String userId) {

        // Inizializza array per la somma dei valori di cibo, medicine e altro

        double[] sumFood = {0.0};
        double[] sumMedicines = {0.0};
        double[] sumOther = {0.0};

        Log.d("QueryDates", "Start date before try-catch: " + startDate + ", End date: " + endDate);

        // Verifica se le date di inizio e fine sono state specificate

        if (startDate != null && endDate != null) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            List<String> datesInRange = new ArrayList<>();

            try {

                // Converte le date di inizio e fine in oggetti Date

                Date start = format.parse(startDate);
                Date end = format.parse(endDate);

                Log.d("QueryDates", "Start date: " + startDate + ", End date: " + endDate);

                Calendar calendarStart = Calendar.getInstance();
                if (start != null) {
                    calendarStart.setTime(start);
                }

                Calendar calendarEnd = Calendar.getInstance();
                if (end != null) {
                    calendarEnd.setTime(end);
                }

                // Genera una lista di date nell'intervallo specificato

                while (!calendarStart.after(calendarEnd)) {
                    Date currentDate = calendarStart.getTime();
                    String formattedDate = format.format(currentDate);
                    datesInRange.add(formattedDate);
                    calendarStart.add(Calendar.DATE, 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("DateParseError", "Error parsing dates", e);
            }

            Log.d("DatesInRange", "Dates in Range: " + datesInRange);

            // Ottiene un riferimento al documento su Firestore

            DocumentReference docRef = db.collection("Chart Pie Data")
                    .document(userId + " FMO");

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Log.d("Fire store Document", "Document Snapshot: " + documentSnapshot.getData());

                        // Calcola la somma dei valori per cibo, medicine e altro per le date specificate

                        for (String date : datesInRange) {
                            Object rawData = documentSnapshot.get(date);

                            if (rawData instanceof List<?>) {
                                List<?> rawList = (List<?>) rawData;
                                if (rawList.size() >= 3 && allElementsAreNumbers(rawList)) {
                                    sumFood[0] += ((Number) rawList.get(0)).doubleValue();
                                    sumMedicines[0] += ((Number) rawList.get(1)).doubleValue();
                                    sumOther[0] += ((Number) rawList.get(2)).doubleValue();
                                }
                            }
                        }

                        // Crea un array di valori per il grafico a torta

                        double[] valuesArray = new double[]{
                                sumFood[0],
                                sumMedicines[0],
                                sumOther[0]
                        };

                        Log.d("SummedValues", "Food: " + sumFood[0] + ", Medicines: "
                                + sumMedicines[0] + ", Other: " + sumOther[0]);

                        // Configura e visualizza il grafico a torta con i valori calcolati

                        setupPieChart(valuesArray);
                    } else {
                        Log.d("Fire store Error", "Document does not exist");
                    }
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        e.printStackTrace();
                        Log.d("Fire store Error", "Error fetching document: " + e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * Restituisce una data formattata nel formato "yyyy-MM-dd" a partire dall'anno, mese e giorno forniti.
     *
     * @param year  Anno
     * @param month Mese (0-based)
     * @param day   Giorno del mese
     * @return Data formattata
     */
    private String getCurrentFormattedDate(int year, int month, int day) {
        return String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month + 1, day);
    }

    /**
     * Verifica se tutti gli elementi nella lista sono numeri.
     *
     * @param list Lista di elementi
     * @return true se tutti gli elementi sono numeri, altrimenti false
     */
    private boolean allElementsAreNumbers(List<?> list) {
        for (Object element : list) {
            if (!(element instanceof Number)) {
                return false;
            }
        }
        return true;
    }

    public void loadPieChart() {
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        DocumentReference docRef = db.collection("Chart Pie Data")
                .document(userId + " FMO");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Double> values = new ArrayList<>();

                // Ottiene i dati dalla data formattata corrente
                //Controlla se i valori sono una istanza di Double e Integer

                Object rawData = documentSnapshot.get(formattedDate);
                if (rawData instanceof List<?>) {
                    List<?> rawList = (List<?>) rawData;

                    for (Object item : rawList) {
                        if (item instanceof Double) {
                            values.add((Double) item);
                        } else if (item instanceof Integer) {
                            values.add(((Integer) item).doubleValue());
                        }
                    }
                }
                if (values.size() >= 3) {
                    double[] valuesArray = new double[]{
                            values.get(0),
                            values.get(1),
                            values.get(2)
                    };
                    setupPieChart(valuesArray);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPieChart();
    }
}