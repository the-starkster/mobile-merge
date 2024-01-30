package com.example.asilapp10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class FragmentHealthProfile extends Fragment {

    // Queste chiavi servono per poter prendere i valori contenuti nei campi in Firestore
    private static final String KEY_HEART = "Heartbeat rate";
    public static final String KEY_PRESSURE = "Pressure";
    public static final String KEY_DIABETES = "Diabetes";
    public static final String KEY_RESPIRATORY_RATE = "Respiratory rate";
    public static final String KEY_OXYGENATION = "Oxygenation";
    public static final String KEY_TEMPERATURE = "Body temperature";
    public static final String KEY_PRESSURE_DATE = "Pressure measurement data";
    public static final String KEY_DIABETES_DATE = "Diabetes measurement data";
    public static final String KEY_RESPIRATORY_RATE_DATE = "Respiratory rate measurement data";
    public static final String KEY_HEARTBEAT_DATE = "Heartbeat measurement data";
    public static final String KEY_OXYGENATION_DATE = "Oxygenation data";
    public static final String KEY_TEMPERATURE_DATE = "Body temperature data";
    public static final String KEY_NUMBER_EDIT_TEXT = "Number Edit Text";
    public static final String KEY_ID_DOCUMENT = "LsYRSc0yPM8h7PoN9Pmy";
    public static final String KEY_IDS_DELETED = "IDs deleted";
    private static final String KEY_SMOKE = "Do you smoke?";
    private static final String KEY_DRUGS = "Do you do drugs?";
    private static final String KEY_DRINK = "Do you drink?";
    private static final String KEY_MEDICINES = "Are you on medicines?";
    private static final String KEY_OPERATED = "Have you ever been operated in the past?";
    private static final String KEY_FAMILY_DISEASES = "Has anyone in your family had any diseases?";
    private static final String KEY_EXERCISE = "Do you exercise?";
    private static final String KEY_GOOD_DIET = "Do you have a good diet?";
    private static final String KEY_SLEEP = "Do you sleep enough?";
    private static final String KEY_ALLERGIES = "Do you have any allergies?";
    private static final String KEY_WATER = "Do you drink enough water?";
    private static final String KEY_MEALS_EAT_DAY = "How many meals do you eat in a day?";
    private static final String KEY_VEGETABLES_FRUIT = "Do you eat enough vegetables and fruit?";
    private static final String KEY_DIET_ON = "Which diet are you on?";

    FirebaseUser user;
    FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Variabili relative al cuore (Heart)
    TextView tHeartData, tMeasureDataHeart, tDateHeart, tDataMeasureHeartGone, heartbeat;

    // Variabili relative alla pressione (Pressure)
    TextView tPressureData, tMeasureDataPressure, tDatePressure, tDataMeasurePressureGone, pressure;

    // Variabili relative al diabete (Diabetes)
    TextView tDiabetesData, tMeasureDataDiabetes, tDateDiabetes, tDataMeasureDiabetesGone, diabetes;

    // Variabili relative alla respirazione (Respiratory)
    TextView tRespiratoryData, tMeasureDataRespiratory, tDateRespiratory, tDataMeasureRespiratoryGone, respiratory;

    // Variabili relative all'ossigenazione (Oxygenation)
    TextView tOxygenationData, tMeasureDataOxygenation, tDateOxygenation, tDataMeasureOxygenationGone, oxygenation;

    // Variabili relative alla temperatura (Temperature)
    TextView tTemperatureData, tMeasureDataTemperature, tDateTemperature, tDataMeasureTemperatureGone, temperature;

    // Variabili per EditText, ImageView, TextView, Button, LinearLayout e SensorEventListener
    TextView tInfoSensor;

    EditText eMedicines, eDrugs, eDrinkWater, eDrink, eEatVegetablesAndFruit, eExercise,
             eGoodDiet, eAllergies, eSleep, eSmoke, eFamilyDiseases, eOperated, eMealsADay,
             eDietOn;
    ImageView imageViewSensorLocation;

    Button buttonBack, buttonMeasure, buttonMeasureHeart, buttonMeasurePressure,
           buttonMeasureDiabetes, buttonMeasureRespiratory, buttonBackMeasureHeart,
           buttonMeasureOxygenation, buttonMeasureTemperature, buttonAddEditText, buttonEditEditText,
           buttonBackEditText, buttonSaveEditText, buttonBackHealth, buttonShareHealth, buttonEditHealth,
           buttonApplyHealth, buttonShareMeasurement, buttonSharePathologies;

    LinearLayout container, containerThree;
    SensorEventListener proximitySensorListener;
    int countEditText; // Contiene il numero di EditText
    boolean flag = true; //Evita che gli EditText si sdoppiano

    private final Map<Integer, EditText> editTextMap = new HashMap<>(); // Contiene gli EditText

    public FragmentHealthProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_healt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        String userId = (user != null) ? user.getUid() : "";

        // Inizializzo la variabile counterEditText

        DocumentReference docRef = db.collection("Pathologies")
                .document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Long count = document.getLong(KEY_NUMBER_EDIT_TEXT);
                    if (count != null) {
                        countEditText = count.intValue();
                    } else {
                        // Gestisci il caso in cui il valore sia null
                        Log.d("Firestore", "Il valore di KEY_NUMBER_EDIT_TEXT è null");
                    }
                } else {
                    Log.d("Firestore", "Nessun documento trovato");
                }
            } else {
                Log.d("Firestore", "Errore nel recupero del documento", task.getException());
            }
        });


        container = requireView().findViewById(R.id.container);
        containerThree = requireView().findViewById(R.id.container_section_three);

        disableAllViews(containerThree);

        queryHealthData(userId);

        imageViewSensorLocation = requireView().findViewById(R.id.imageViewSensorLocation);
        tInfoSensor = requireView().findViewById(R.id.info_sensor);

        buttonBack = requireView().findViewById(R.id.btn_back_measurement);
        buttonAddEditText = requireView().findViewById(R.id.btn_add_edit_text);
        buttonEditEditText = requireView().findViewById(R.id.btn_edit_edit_text);
        buttonBackEditText = requireView().findViewById(R.id.btn_back_edit_text);
        buttonSaveEditText = requireView().findViewById(R.id.btn_save_edit_text);

        buttonBackMeasureHeart = requireView().findViewById(R.id.btn_back_measurement_heart);
        buttonMeasure = requireView().findViewById(R.id.btn_measure);
        buttonMeasureHeart = requireView().findViewById(R.id.btn_measure_heart);
        buttonMeasurePressure = requireView().findViewById(R.id.btn_measure_pressure);
        buttonMeasureDiabetes = requireView().findViewById(R.id.btn_measure_diabetes);
        buttonMeasureRespiratory = requireView().findViewById(R.id.btn_measure_respiratory_rate);
        buttonMeasureOxygenation = requireView().findViewById(R.id.btn_measure_oxygenation);
        buttonMeasureTemperature = requireView().findViewById(R.id.btn_measure_temperature);

        buttonBackHealth = requireView().findViewById(R.id.btn_back_health);
        buttonShareHealth = requireView().findViewById(R.id.btn_share_health);
        buttonEditHealth = requireView().findViewById(R.id.btn_edit_health);
        buttonApplyHealth = requireView().findViewById(R.id.btn_apply_health);
        buttonShareMeasurement = requireView().findViewById(R.id.btn_share_measurement);

        tDataMeasureHeartGone = requireView().findViewById(R.id.data_measure_heart);
        tHeartData = requireView().findViewById(R.id.t_heart);
        tMeasureDataHeart = requireView().findViewById(R.id.heart_date);
        tDateHeart = requireView().findViewById(R.id.t_heart_date);
        heartbeat = requireView().findViewById(R.id.heartbeat);

        tDataMeasurePressureGone = requireView().findViewById(R.id.data_measure_pressure);
        tPressureData = requireView().findViewById(R.id.t_pressure);
        tMeasureDataPressure = requireView().findViewById(R.id.pressure_date);
        tDatePressure = requireView().findViewById(R.id.t_pressure_date);
        pressure = requireView().findViewById(R.id.pressure);

        tDataMeasureDiabetesGone = requireView().findViewById(R.id.data_measure_diabetes);
        tDiabetesData = requireView().findViewById(R.id.t_diabetes);
        tMeasureDataDiabetes = requireView().findViewById(R.id.diabetes_date);
        tDateDiabetes = requireView().findViewById(R.id.t_diabetes_date);
        diabetes = requireView().findViewById(R.id.diabetes);

        tDataMeasureRespiratoryGone = requireView().findViewById(R.id.data_measure_respiratory_rate);
        tRespiratoryData = requireView().findViewById(R.id.t_respiratory_rate);
        tMeasureDataRespiratory = requireView().findViewById(R.id.respratory_rate_date);
        tDateRespiratory = requireView().findViewById(R.id.t_respiratory_rate_date);
        respiratory = requireView().findViewById(R.id.respiratory);

        tDataMeasureOxygenationGone = requireView().findViewById(R.id.data_measure_oxygenation);
        tOxygenationData = requireView().findViewById(R.id.t_oxygenation);
        tMeasureDataOxygenation = requireView().findViewById(R.id.oxygenation_date);
        tDateOxygenation = requireView().findViewById(R.id.t_oxygenation_date);
        oxygenation = requireView().findViewById(R.id.oxygenation);

        tDataMeasureTemperatureGone = requireView().findViewById(R.id.data_measure_temperature);
        tTemperatureData = requireView().findViewById(R.id.t_temperature);
        tMeasureDataTemperature = requireView().findViewById(R.id.temperature_date);
        tDateTemperature = requireView().findViewById(R.id.t_temperature_date);
        temperature = requireView().findViewById(R.id.temperature);

        eMedicines = requireView().findViewById(R.id.health_medicines);
        eDrugs = requireView().findViewById(R.id.health_drugs);
        eDrinkWater = requireView().findViewById(R.id.health_water);
        eDrink = requireView().findViewById(R.id.health_drink);
        eEatVegetablesAndFruit = requireView().findViewById(R.id.health_vegetables_and_fruit);
        eExercise = requireView().findViewById(R.id.health_exercise);
        eGoodDiet = requireView().findViewById(R.id.health_diet);
        eAllergies = requireView().findViewById(R.id.health_allergies);
        eSleep = requireView().findViewById(R.id.health_sleep);
        eSmoke = requireView().findViewById(R.id.health_smoke);
        eFamilyDiseases = requireView().findViewById(R.id.health_family_diseases);
        eOperated = requireView().findViewById(R.id.health_operated);
        eMealsADay = requireView().findViewById(R.id.health_eat);
        eDietOn = requireView().findViewById(R.id.health_diet_on);

        buttonBackHealth = requireView().findViewById(R.id.btn_back_health);
        buttonShareHealth = requireView().findViewById(R.id.btn_share_health);
        buttonEditHealth = requireView().findViewById(R.id.btn_edit_health);
        buttonApplyHealth = requireView().findViewById(R.id.btn_apply_health);
        buttonSharePathologies = requireView().findViewById(R.id.btn_share_pathologies);

        queryLifeStyle(userId);


        // Imposta la visibilità di alcuni widget
        buttonMeasure.setOnClickListener(v -> {

                // Impostare la visibilità a GONE
                tDataMeasureHeartGone.setVisibility(View.VISIBLE);
                tHeartData.setVisibility(View.GONE);
                tMeasureDataHeart.setVisibility(View.GONE);
                tDateHeart.setVisibility(View.GONE);

                tDataMeasurePressureGone.setVisibility(View.VISIBLE);
                tPressureData.setVisibility(View.GONE);
                tMeasureDataPressure.setVisibility(View.GONE);
                tDatePressure.setVisibility(View.GONE);

                tDataMeasureDiabetesGone.setVisibility(View.VISIBLE);
                tDiabetesData.setVisibility(View.GONE);
                tMeasureDataDiabetes.setVisibility(View.GONE);
                tDateDiabetes.setVisibility(View.GONE);

                tDataMeasureRespiratoryGone.setVisibility(View.VISIBLE);
                tRespiratoryData.setVisibility(View.GONE);
                tMeasureDataRespiratory.setVisibility(View.GONE);
                tDateRespiratory.setVisibility(View.GONE);

                tDataMeasureOxygenationGone.setVisibility(View.VISIBLE);
                tOxygenationData.setVisibility(View.GONE);
                tMeasureDataOxygenation.setVisibility(View.GONE);
                tDateOxygenation.setVisibility(View.GONE);

                tDataMeasureTemperatureGone.setVisibility(View.VISIBLE);
                tTemperatureData.setVisibility(View.GONE);
                tMeasureDataTemperature.setVisibility(View.GONE);
                tDateTemperature.setVisibility(View.GONE);

                // Impostare la visibilità a VISIBLE
                buttonShareMeasurement.setVisibility(View.GONE);
                buttonBack.setVisibility(View.VISIBLE);
                buttonMeasure.setVisibility(View.GONE);
                buttonMeasureHeart.setVisibility(View.VISIBLE);
                buttonMeasurePressure.setVisibility(View.VISIBLE);
                buttonMeasureDiabetes.setVisibility(View.VISIBLE);
                buttonMeasureRespiratory.setVisibility(View.VISIBLE);
                buttonMeasureOxygenation.setVisibility(View.VISIBLE);
                buttonMeasureTemperature.setVisibility(View.VISIBLE);

        });

        // Imposta la visibilità di alcuni widget
        buttonBack.setOnClickListener(v -> {

                // Impostare la visibilità a GONE
                tDataMeasureHeartGone.setVisibility(View.GONE);
                tHeartData.setVisibility(View.VISIBLE);
                tMeasureDataHeart.setVisibility(View.VISIBLE);
                tDateHeart.setVisibility(View.VISIBLE);
                heartbeat.setVisibility(View.VISIBLE);

                tDataMeasurePressureGone.setVisibility(View.GONE);
                tPressureData.setVisibility(View.VISIBLE);
                tMeasureDataPressure.setVisibility(View.VISIBLE);
                tDatePressure.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);

                tDataMeasureDiabetesGone.setVisibility(View.GONE);
                tDiabetesData.setVisibility(View.VISIBLE);
                tMeasureDataDiabetes.setVisibility(View.VISIBLE);
                tDateDiabetes.setVisibility(View.VISIBLE);
                diabetes.setVisibility(View.VISIBLE);

                tDataMeasureRespiratoryGone.setVisibility(View.GONE);
                tRespiratoryData.setVisibility(View.VISIBLE);
                tMeasureDataRespiratory.setVisibility(View.VISIBLE);
                tDateRespiratory.setVisibility(View.VISIBLE);
                respiratory.setVisibility(View.VISIBLE);

                tDataMeasureOxygenationGone.setVisibility(View.GONE);
                tOxygenationData.setVisibility(View.VISIBLE);
                tMeasureDataOxygenation.setVisibility(View.VISIBLE);
                tDateOxygenation.setVisibility(View.VISIBLE);

                tDataMeasureTemperatureGone.setVisibility(View.GONE);
                tTemperatureData.setVisibility(View.VISIBLE);
                tMeasureDataTemperature.setVisibility(View.VISIBLE);
                tDateTemperature.setVisibility(View.VISIBLE);

                imageViewSensorLocation.setVisibility(View.GONE);
                tInfoSensor.setVisibility(View.GONE);

                // Impostare la visibilità a VISIBLE
                buttonShareMeasurement.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.GONE);
                buttonMeasure.setVisibility(View.VISIBLE);
                buttonMeasureHeart.setVisibility(View.GONE);
                buttonMeasurePressure.setVisibility(View.GONE);
                buttonMeasureDiabetes.setVisibility(View.GONE);
                buttonMeasureRespiratory.setVisibility(View.GONE);
                buttonMeasureOxygenation.setVisibility(View.GONE);
                buttonMeasureTemperature.setVisibility(View.GONE);

                flag = false;

                queryHealthData(userId);
        });

        buttonBackMeasureHeart.setOnClickListener(v -> showOtherMeasurement());

        //I vari bottoni chiamano le funzioni per simulare le misurazioni mediche

        buttonMeasureHeart.setOnClickListener(v -> {
            showImage();
            simulateAndDisplayHeartbeatRate(userId);
        });

        buttonMeasurePressure.setOnClickListener(v -> simulateAndDisplayBloodPressure(userId));

        buttonMeasureDiabetes.setOnClickListener(v -> simulateAndDisplayDiabetes(userId));

        buttonMeasureRespiratory.setOnClickListener(v -> simulateAndDisplayRespiratoryRate(userId));

        buttonMeasureOxygenation.setOnClickListener(v -> simulateAndDisplayOxygenation(userId));

        buttonMeasureTemperature.setOnClickListener(v -> simulateAndDisplayTemperature(userId));

        // I vari bottoni servono per modificare, aggiungere e salvare gli EditText

        buttonEditEditText.setOnClickListener(v -> {
            // Crea l'EditText per la password
            EditText passwordEditText = new EditText(getContext());
            passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            // Crea l'AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Insert Medical Code");
            builder.setView(passwordEditText);

            builder.setPositiveButton("CONFIRM", (dialog, which) -> {
                String doctorCode = passwordEditText.getText().toString();
                doctorCode = doctorCode.isEmpty() ? "0000" : doctorCode;
                queryMedicalCode(doctorCode, isMatch -> {
                    if (isMatch) {
                        // Codice corrispondente
                        buttonEditEditText.setVisibility(View.GONE);
                        buttonAddEditText.setVisibility(View.VISIBLE);
                        buttonBackEditText.setVisibility(View.VISIBLE);
                        buttonSaveEditText.setVisibility(View.VISIBLE);

                        enableAllViews(container);
                    } else {
                        // Nessun codice trovato
                        Toast.makeText(getActivity(), "No code found!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

            // Mostra l'AlertDialog
            builder.show();
        });

        buttonAddEditText.setOnClickListener(v -> addEditTextAndDeleteButton(userId));

        buttonBackEditText.setOnClickListener(v -> {
            disableAllViews(container);

            buttonEditEditText.setVisibility(View.VISIBLE);
            buttonAddEditText.setVisibility(View.GONE);
            buttonBackEditText.setVisibility(View.GONE);
            buttonSaveEditText.setVisibility(View.GONE);
        });

        buttonSaveEditText.setOnClickListener(v -> saveData(userId));

        // Imposta il listener per il pulsante 'Edit'
        buttonEditHealth.setOnClickListener(v -> {

            // Abilita i campi di testo per la modifica
            enableAllViews(containerThree);


            // Cambia la visibilità dei pulsanti per l'editing
            buttonEditHealth.setVisibility(View.GONE);
            buttonShareMeasurement.setVisibility(View.GONE);

            // Mostra i pulsanti 'Back' e 'Apply Edit'
            buttonBack.setVisibility(View.VISIBLE);
            buttonApplyHealth.setVisibility(View.VISIBLE);
        });

        // Imposta il listener per il pulsante 'Apply Edit'
        buttonApplyHealth.setOnClickListener(v -> {

            // Disabilita i campi di testo dopo la modifica
            disableAllViews(containerThree);


            // Mostra i pulsanti 'Edit', 'Logout' e 'Share'
            buttonEditHealth.setVisibility(View.VISIBLE);
            buttonShareMeasurement.setVisibility(View.VISIBLE);

            // Nasconde i pulsanti 'Back' e 'Apply Edit'
            buttonBack.setVisibility(View.GONE);
            buttonApplyHealth.setVisibility(View.GONE);

            // Salva i nuovi dati dell'utente
            saveNewLifeStyleUser(userId);
        });

        // Imposta il listener per il pulsante 'Back'
        buttonBackHealth.setOnClickListener(v -> {

            // Disabilita i campi di testo
            disableAllViews(containerThree);

            // Mostra i pulsanti 'Edit', 'Logout' e 'Share'
            buttonEditHealth.setVisibility(View.VISIBLE);
            buttonShareHealth.setVisibility(View.VISIBLE);

            // Nasconde i pulsanti 'Back' e 'Apply Edit'
            buttonBackHealth.setVisibility(View.GONE);
            buttonApplyHealth.setVisibility(View.GONE);
        });

        // Imposta il listener per il pulsante 'Share'
        buttonShareHealth.setOnClickListener(v -> {

            // Crea un AlertDialog per confermare la condivisione
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm sharing");
            builder.setMessage("Do you want to share your life style data?");

            // Imposta i pulsanti di risposta nell'AlertDialog
            builder.setPositiveButton("YES", (dialog, id) -> shareLifeStyleUser());
            builder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());

            // Mostra l'AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //Utilizzato per condividere le misurazioni
        buttonShareMeasurement.setOnClickListener(v ->{

            // Crea un AlertDialog per confermare la condivisione
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm sharing");
            builder.setMessage("Do you want to share your measurement?");

            // Imposta i pulsanti di risposta nell'AlertDialog
            builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> shareMeasurement());
            builder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());

            // Mostra l'AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //Utilizzato per condividere le patologie
        buttonSharePathologies.setOnClickListener(v ->{

            // Crea un AlertDialog per confermare la condivisione
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.confirm_sharing));
            builder.setMessage("Do you want to share your pathologies?");

            // Imposta i pulsanti di risposta nell'AlertDialog
            builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> sharePathologies());
            builder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());

            // Mostra l'AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    /**
     * Simula e visualizza la frequenza cardiaca utilizzando il sensore di prossimità del dispositivo.
     * Questa funzione registra un listener per il sensore di prossimità. Quando il sensore rileva
     * un oggetto vicino (come un dito), genera un valore casuale per la frequenza cardiaca tra 60 e 130 bpm.
     * Questo valore viene poi visualizzato in una TextView e salvato nel database sotto
     * "User Measurement Data" con la data corrente. In caso di successo, viene mostrato un messaggio di completamento;
     * in caso di errore, viene mostrato un messaggio di errore. Il listener viene disattivato dopo la prima rilevazione.
     *
     * @param userId L'ID dell'utente per cui viene simulata la frequenza cardiaca, usato per salvare i dati nel database.
     */
    public void simulateAndDisplayHeartbeatRate(String userId){
        final int[] heartRate = {0};
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                assert proximitySensor != null;
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    sensorManager.unregisterListener(proximitySensorListener);
                    // Il dito è vicino al sensore, genera un valore per la frequenza cardiaca
                    heartRate[0] = new Random().nextInt((130 - 60) + 1) + 60; // Valore casuale tra 60 e 100
                    // Mostra o utilizza il valore di heartRate
                    String heartbeatRateText = heartRate[0] + " bpm";

                    LocalDate currentDate = LocalDate.now();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                    String formattedDate = currentDate.format(formatter);

                    showOtherMeasurement();

                    // Mostra la stringa nella TextView
                    tDataMeasureHeartGone.setText(heartbeatRateText);
                    tDateHeart.setText(formattedDate);

                    Map<String, Object> note =  new HashMap<>();

                    note.put(KEY_HEART, heartbeatRateText);
                    note.put(KEY_HEARTBEAT_DATE, formattedDate);

                    db.collection("User Measurement Data").document(userId + " Health Data")
                            .update(note)
                            .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Calculation completed!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    /**
     * Visualizza l'immagine del sensore e le informazioni relative, mentre nasconde le altre View.
     * Questa funzione rende visibili l'imageView del sensore e il testo informativo, e mostra
     * il pulsante per tornare alla misurazione della frequenza cardiaca. Nasconde tutte le TextView
     * relative alle misurazioni di salute (come frequenza cardiaca, pressione, ecc.) e i pulsanti
     * per le misurazioni specifiche. È utilizzata per focalizzare l'attenzione dell'utente
     * sul posizionamento del sensore.
     */
    public void showImage(){
        imageViewSensorLocation.setVisibility(View.VISIBLE);
        tInfoSensor.setVisibility(View.VISIBLE);
        buttonBackMeasureHeart.setVisibility(View.VISIBLE);
        buttonBack.setVisibility(View.GONE);

        tDataMeasureHeartGone.setVisibility(View.GONE);
        tHeartData.setVisibility(View.GONE);
        tMeasureDataHeart.setVisibility(View.GONE);
        tDateHeart.setVisibility(View.GONE);
        heartbeat.setVisibility(View.GONE);

        tDataMeasurePressureGone.setVisibility(View.GONE);
        tPressureData.setVisibility(View.GONE);
        tMeasureDataPressure.setVisibility(View.GONE);
        tDatePressure.setVisibility(View.GONE);
        pressure.setVisibility(View.GONE);

        tDataMeasureDiabetesGone.setVisibility(View.GONE);
        tDiabetesData.setVisibility(View.GONE);
        tMeasureDataDiabetes.setVisibility(View.GONE);
        tDateDiabetes.setVisibility(View.GONE);
        diabetes.setVisibility(View.GONE);

        tDataMeasureRespiratoryGone.setVisibility(View.GONE);
        tRespiratoryData.setVisibility(View.GONE);
        tMeasureDataRespiratory.setVisibility(View.GONE);
        tDateRespiratory.setVisibility(View.GONE);
        respiratory.setVisibility(View.GONE);

        tDataMeasureOxygenationGone.setVisibility(View.GONE);
        tOxygenationData.setVisibility(View.GONE);
        tMeasureDataOxygenation.setVisibility(View.GONE);
        tDateOxygenation.setVisibility(View.GONE);
        oxygenation.setVisibility(View.GONE);

        tDataMeasureTemperatureGone.setVisibility(View.GONE);
        tTemperatureData.setVisibility(View.GONE);
        tMeasureDataTemperature.setVisibility(View.GONE);
        tDateTemperature.setVisibility(View.GONE);
        temperature.setVisibility(View.GONE);

        // Impostare la visibilità a VISIBLE
        buttonMeasureHeart.setVisibility(View.GONE);
        buttonMeasurePressure.setVisibility(View.GONE);
        buttonMeasureDiabetes.setVisibility(View.GONE);
        buttonMeasureRespiratory.setVisibility(View.GONE);
        buttonMeasureOxygenation.setVisibility(View.GONE);
        buttonMeasureTemperature.setVisibility(View.GONE);
    }

    /**
     * Visualizza le opzioni per altre misurazioni, nascondendo l'immagine del sensore e le informazioni.
     * Questa funzione nasconde l'imageView del sensore e il testo informativo. Rende visibili
     * i pulsanti per le misurazioni di salute (come pressione, diabete, ecc.) e visualizza le TextView
     * per le misurazioni completate. È utilizzata per permettere all'utente di passare a diverse
     * misurazioni di salute dopo aver completato una misurazione specifica.
     */
    public void showOtherMeasurement(){

        imageViewSensorLocation.setVisibility(View.GONE);
        tInfoSensor.setVisibility(View.GONE);

        tDataMeasureHeartGone.setVisibility(View.VISIBLE);
        tHeartData.setVisibility(View.GONE);
        tMeasureDataHeart.setVisibility(View.GONE);
        tDateHeart.setVisibility(View.GONE);
        heartbeat.setVisibility(View.VISIBLE);

        tDataMeasurePressureGone.setVisibility(View.VISIBLE);
        tPressureData.setVisibility(View.GONE);
        tMeasureDataPressure.setVisibility(View.GONE);
        tDatePressure.setVisibility(View.GONE);
        pressure.setVisibility(View.VISIBLE);

        tDataMeasureDiabetesGone.setVisibility(View.VISIBLE);
        tDiabetesData.setVisibility(View.GONE);
        tMeasureDataDiabetes.setVisibility(View.GONE);
        tDateDiabetes.setVisibility(View.GONE);
        diabetes.setVisibility(View.VISIBLE);

        tDataMeasureRespiratoryGone.setVisibility(View.VISIBLE);
        tRespiratoryData.setVisibility(View.GONE);
        tMeasureDataRespiratory.setVisibility(View.GONE);
        tDateRespiratory.setVisibility(View.GONE);
        respiratory.setVisibility(View.VISIBLE);

        tDataMeasureOxygenationGone.setVisibility(View.VISIBLE);
        tOxygenationData.setVisibility(View.GONE);
        tMeasureDataOxygenation.setVisibility(View.GONE);
        tDateOxygenation.setVisibility(View.GONE);
        oxygenation.setVisibility(View.VISIBLE);

        tDataMeasureTemperatureGone.setVisibility(View.VISIBLE);
        tTemperatureData.setVisibility(View.GONE);
        tMeasureDataTemperature.setVisibility(View.GONE);
        tDateTemperature.setVisibility(View.GONE);
        temperature.setVisibility(View.VISIBLE);

        // Impostare la visibilità a VISIBLE
        buttonShareMeasurement.setVisibility(View.GONE);
        buttonBack.setVisibility(View.VISIBLE);
        buttonBackMeasureHeart.setVisibility(View.GONE);
        buttonMeasure.setVisibility(View.GONE);
        buttonMeasureHeart.setVisibility(View.VISIBLE);
        buttonMeasurePressure.setVisibility(View.VISIBLE);
        buttonMeasureDiabetes.setVisibility(View.VISIBLE);
        buttonMeasureRespiratory.setVisibility(View.VISIBLE);
        buttonMeasureOxygenation.setVisibility(View.VISIBLE);
        buttonMeasureTemperature.setVisibility(View.VISIBLE);
    }

    /**
     * Simula e visualizza la pressione sanguigna dell'utente.
     * Genera valori casuali per la pressione sistolica e diastolica e li visualizza in una TextView.
     * Salva questi valori nel database sotto "User Measurement Data" con la data corrente.
     * Mostra un messaggio di successo o errore a seconda dell'esito del salvataggio.
     *
     * @param userId L'ID dell'utente per cui viene simulata la pressione sanguigna.
     */

    public void simulateAndDisplayBloodPressure(String userId){
        Random rand = new Random();

        // Genera valori casuali per la pressione sistolica e diastolica
        int sistolic = rand.nextInt((200 - 80) + 1) + 80; // Intervallo: 80-200 mmHg
        int diastolic = rand.nextInt((120 - 40) + 1) + 40; // Intervallo: 40-120 mmHg

        // Costruisci la stringa da mostrare
        String bloodPressureText = "Max: " + sistolic + " mmHg\n" +
                "Min: " + diastolic + " mmHg";

        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String formattedDate = currentDate.format(formatter);

        // Mostra la stringa nella TextView
        tDataMeasurePressureGone.setText(bloodPressureText);
        tDatePressure.setText(formattedDate);

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_PRESSURE, bloodPressureText);
        note.put(KEY_PRESSURE_DATE, formattedDate);

        db.collection("User Measurement Data").document(userId + " Health Data")
                .update(note)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "User data saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Simula e visualizza il livello di glucosio nel sangue dell'utente.
     * Genera un valore casuale per il glucosio e lo visualizza in una TextView.
     * Salva questo valore nel database sotto "User Measurement Data" con la data corrente.
     * Mostra un messaggio di successo o errore a seconda dell'esito del salvataggio.
     *
     * @param userId L'ID dell'utente per cui viene simulato il livello di glucosio.
     */

    public void simulateAndDisplayDiabetes(String userId){
        Random rand = new Random();

        // Genera valori casuali per il glucosio nel sangue
        int glucoseLevel = rand.nextInt((250 - 70) + 1) + 70; // Intervallo: 70-250 mg/dL

        String glucoseLevelText = glucoseLevel + " mmol/L";

        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String formattedDate = currentDate.format(formatter);

        // Mostra la stringa nella TextView
        tDataMeasureDiabetesGone.setText(glucoseLevelText);
        tDateDiabetes.setText(formattedDate);

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_DIABETES, glucoseLevelText);
        note.put(KEY_DIABETES_DATE, formattedDate);

        db.collection("User Measurement Data").document(userId + " Health Data")
                .update(note)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "User data saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());


    }

    /**
     * Simula e visualizza la frequenza respiratoria dell'utente.
     * Genera un valore casuale per la frequenza respiratoria e lo visualizza in una TextView.
     * Salva questo valore nel database sotto "User Measurement Data" con la data corrente.
     * Mostra un messaggio di successo o errore a seconda dell'esito del salvataggio.
     *
     * @param userId L'ID dell'utente per cui viene simulata la frequenza respiratoria.
     */

    public void simulateAndDisplayRespiratoryRate(String userId){
        Random rand = new Random();

        // Genera un valore casuale per la frequenza respiratoria
        int respiratoryRate = rand.nextInt((30 - 10) + 1) + 10; // Intervallo: 10-30 respiri al minuto

        String respiratoryRateText = respiratoryRate + " bpm";

        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String formattedDate = currentDate.format(formatter);

        // Mostra la stringa nella TextView
        tDataMeasureRespiratoryGone.setText(respiratoryRateText);
        tDateRespiratory.setText(formattedDate);

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_RESPIRATORY_RATE, respiratoryRateText);
        note.put(KEY_RESPIRATORY_RATE_DATE, formattedDate);

        db.collection("User Measurement Data").document(userId + " Health Data")
                .update(note)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "User data saves!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());

    }

    /**
     * Simula e visualizza il livello di ossigenazione del sangue dell'utente.
     * Genera un valore casuale per l'ossigenazione e lo visualizza in una TextView.
     * Salva questo valore nel database sotto "User Measurement Data" con la data corrente.
     * Mostra un messaggio di successo o errore a seconda dell'esito del salvataggio.
     *
     * @param userId L'ID dell'utente per cui viene simulato il livello di ossigenazione.
     */

    public void simulateAndDisplayOxygenation(String userId){
        Random rand = new Random();

        // Genera un valore casuale per la frequenza respiratoria
        int oxygenation = rand.nextInt(11) + 90; // Intervallo: 90% - 100%

        String oxygenationText = oxygenation + " %";

        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String formattedDate = currentDate.format(formatter);

        // Mostra la stringa nella TextView
        tDataMeasureOxygenationGone.setText(oxygenationText);
        tDateOxygenation.setText(formattedDate);

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_OXYGENATION, oxygenationText);
        note.put(KEY_OXYGENATION_DATE, formattedDate);

        db.collection("User Measurement Data").document(userId + " Health Data")
                .update(note)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "User data saves!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());

    }

    /**
     * Simula e visualizza la temperatura corporea dell'utente.
     * Genera un valore casuale per la temperatura e lo visualizza in una TextView.
     * Salva questo valore nel database sotto "User Measurement Data" con la data corrente.
     * Mostra un messaggio di successo o errore a seconda dell'esito del salvataggio.
     *
     * @param userId L'ID dell'utente per cui viene simulata la temperatura corporea.
     */

    public void simulateAndDisplayTemperature(String userId){
        Random rand = new Random();

        // Genera un valore casuale per la frequenza respiratoria
        float temperature = (4.5f * rand.nextFloat()) + 35.5f; // Intervallo: 35.5 - 40.5
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        String temperatureText = decimalFormat.format(temperature) + " °C";


        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String formattedDate = currentDate.format(formatter);

        // Mostra la stringa nella TextView
        tDataMeasureTemperatureGone.setText(temperatureText);
        tDateTemperature.setText(formattedDate);

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_TEMPERATURE, temperatureText);
        note.put(KEY_TEMPERATURE_DATE, formattedDate);

        db.collection("User Measurement Data").document(userId + " Health Data")
                .update(note)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "User data saves!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());

    }

    /**
     * Interroga i dati sanitari dell'utente da Firestore.
     * Recupera dati come battito cardiaco, pressione, diabete, saturazione di ossigeno, temperatura, e frequenza respiratoria.
     * Mostra i dati recuperati sulle corrispondenti TextView.
     * Gestisce il caso in cui i dati non esistano o ci sia un errore nella loro acquisizione.
     * Inoltre, se abilitato, recupera e visualizza informazioni aggiuntive relative alle patologie.
     *
     * @param userId L'ID dell'utente per il quale si vogliono recuperare i dati.
     */


    public void queryHealthData(String userId){
        DocumentReference docRef = db.collection("User Measurement Data")
                .document(userId + " Health Data");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Qui ottieni i dati dal documento
                    String heart = document.getString(KEY_HEART);
                    String pressure = document.getString(KEY_PRESSURE);
                    String diabetes = document.getString(KEY_DIABETES);
                    String oxygenation = document.getString(KEY_OXYGENATION);
                    String temperature = document.getString(KEY_TEMPERATURE);
                    String respiratoryRate = document.getString(KEY_RESPIRATORY_RATE);
                    String heartDate = document.getString(KEY_HEARTBEAT_DATE);
                    String pressureDate = document.getString(KEY_PRESSURE_DATE);
                    String diabetesDate = document.getString(KEY_DIABETES_DATE);
                    String respiratoryRateDate = document.getString(KEY_RESPIRATORY_RATE_DATE);
                    String oxygenationDate = document.getString(KEY_OXYGENATION_DATE);
                    String temperatureDate = document.getString(KEY_TEMPERATURE_DATE);

                    tHeartData.setText(heart);
                    tDateHeart.setText(heartDate);
                    tPressureData.setText(pressure);
                    tDatePressure.setText(pressureDate);
                    tDiabetesData.setText(diabetes);
                    tDateDiabetes.setText(diabetesDate);
                    tRespiratoryData.setText(respiratoryRate);
                    tDateRespiratory.setText(respiratoryRateDate);
                    tOxygenationData.setText(oxygenation);
                    tDateOxygenation.setText(oxygenationDate);
                    tTemperatureData.setText(temperature);
                    tDateTemperature.setText(temperatureDate);

                } else {
                    Log.d("Firestore", "Nessun documento trovato");
                }
            } else {
                Log.d("Firestore", "Errore nel recupero del documento", task.getException());
            }
        });

        docRef = db.collection("Pathologies").document(userId);

        if (flag) {

            docRef.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()) {

                        // Recupera l'elenco degli ID eliminati
                        Object firestoreDeletedIdsObj = documentSnapshot.get(KEY_IDS_DELETED);
                        List<Long> firestoreDeletedIds = new ArrayList<>();

                        if (firestoreDeletedIdsObj instanceof List<?>) {
                            for (Object item : (List<?>) firestoreDeletedIdsObj) {
                                if (item instanceof Long) {
                                    firestoreDeletedIds.add((Long) item);
                                }
                            }
                        }

                        deletedEditTextIds.clear();
                        deletedEditTextIds.addAll(firestoreDeletedIds.stream()
                                .map(Long::intValue)
                                .collect(Collectors.toList()));


                        Long numberOfEditTextsLong = documentSnapshot.getLong(KEY_NUMBER_EDIT_TEXT);
                        Integer numberOfEditTexts = numberOfEditTextsLong != null ? numberOfEditTextsLong.intValue() : null;

                        if (numberOfEditTexts != null) {
                            for (int i = 1; i <= numberOfEditTexts; i++) {

                                if (deletedEditTextIds.contains(i)) {
                                    continue;
                                }

                                EditText editText = new EditText(getContext());
                                editText.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                editText.setText(documentSnapshot.getString(String.valueOf(i)));
                                editText.setId(i);

                                editTextMap.put(editText.getId(), editText);

                                // Crea il bottone di eliminazione
                                Button deleteButton = new Button(getContext());
                                deleteButton.setText(R.string.delete);
                                deleteButton.setId(i + 1000); // Assicurati che l'ID del bottone sia univoco

                                // Aggiungi il listener al bottone di eliminazione
                                deleteButton.setOnClickListener(v -> {
                                    container.removeView(editText);
                                    container.removeView(deleteButton);
                                    deletedEditTextIds.add(editText.getId());
                                    editTextMap.remove(editText.getId());
                                    updateFireStoreWithIdsDeleted(userId);
                                });

                                // Aggiungi un TextWatcher per tracciare i cambiamenti
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        // Implementazione non necessaria
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        editTextData.put(editText.getId(), s.toString());
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        // Implementazione non necessaria
                                    }
                                });

                                // Imposta il colore del testo a nero
                                editText.setTextColor(Color.BLACK);

                                container.addView(editText);
                                container.addView(deleteButton);
                            }
                            disableAllViews(container);
                        }
                    }
                }
            });
        }
        flag = true;
    }

    /**
     * Interroga il codice medico fornito dall'utente, confrontandolo con un insieme di codici memorizzati in Firestore.
     * Utilizza un callback per comunicare il risultato della verifica (corrispondenza o meno).
     * Gestisce il caso in cui il documento non esista o ci sia un errore nella query.
     *
     * @param doctorCode Il codice medico fornito per la verifica.
     * @param callback Il callback utilizzato per restituire il risultato della verifica.
     */


    public void queryMedicalCode(String doctorCode, QueryCallback callback) {

        db.collection("Medical Code").document(KEY_ID_DOCUMENT).get()
                .addOnCompleteListener(task -> {
                    boolean isMatch = false;
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Object storedPasswordObj = document.get("Doctor Code");

                            List<Long> storedPassword = new ArrayList<>();
                            if (storedPasswordObj instanceof List<?>) {
                                for (Object item : (List<?>) storedPasswordObj) {
                                    if (item instanceof Long) {
                                        storedPassword.add((Long) item);
                                    }
                                }
                            }

                            // Converte l'input dell'utente in un intero
                            int userInput = Integer.parseInt(doctorCode);

                            // Controlla se l'input corrisponde a uno degli elementi dell'array 'password'
                            for (Long password : storedPassword) {
                                if (userInput == password.intValue()) {
                                    isMatch = true;
                                    break;
                                }
                            }

                            if (!isMatch) {
                                Log.d("queryMedicalCode", "Non corrispondenza trovata");
                            }
                        } else {
                            Log.d("queryMedicalCode", "Il documento non esiste");
                        }
                    } else {
                        Log.e("queryMedicalCode", "Errore durante la query", task.getException());
                    }
                    // Chiamata al callback
                    callback.onQueryCompleted(isMatch);
                });

    }

    /**
     * Interfaccia di callback utilizzata per restituire i risultati di queryMedicalCode.
     */

    public interface QueryCallback {
        void onQueryCompleted(boolean isMatch);
    }

    /**
     * Interroga i dati relativi allo stile di vita dell'utente da Firestore.
     * Recupera informazioni come abitudini alimentari, esercizio fisico, allergie, sonno, fumo, ecc.
     * Visualizza queste informazioni sulle corrispondenti TextView.
     * Gestisce il caso in cui il documento non esista o ci sia un errore nella sua acquisizione.
     *
     * @param userId L'ID dell'utente per il quale si vogliono recuperare i dati sullo stile di vita.
     */


    private void queryLifeStyle(String userId) {

        DocumentReference docRef = db.collection("Lifestyle Users")
                .document(userId + " Lifestyle");

        // Effettua la interrogazione
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("FirestoreDocument", "Document Snapshot: " + documentSnapshot.getData());

                    // Imposta i dati sulle TextView
                    eMedicines.setText(documentSnapshot.getString(KEY_MEDICINES));
                    eDrugs.setText(documentSnapshot.getString(KEY_DRUGS));
                    eDrinkWater.setText(documentSnapshot.getString(KEY_WATER));
                    eDrink.setText(documentSnapshot.getString(KEY_DRINK));
                    eEatVegetablesAndFruit.setText(documentSnapshot.getString(KEY_VEGETABLES_FRUIT));
                    eExercise.setText(documentSnapshot.getString(KEY_EXERCISE));
                    eGoodDiet.setText(documentSnapshot.getString(KEY_GOOD_DIET));
                    eAllergies.setText(documentSnapshot.getString(KEY_ALLERGIES));
                    eSleep.setText(documentSnapshot.getString(KEY_SLEEP));
                    eSmoke.setText(documentSnapshot.getString(KEY_SMOKE));
                    eFamilyDiseases.setText(documentSnapshot.getString(KEY_FAMILY_DISEASES));
                    eOperated.setText(documentSnapshot.getString(KEY_OPERATED));
                    eMealsADay.setText(documentSnapshot.getString(KEY_MEALS_EAT_DAY));
                    eDietOn.setText(documentSnapshot.getString(KEY_DIET_ON));


                } else {
                    // Il documento non esiste
                    Log.d("FirestoreError", "Document does not exist");
                }
            } else {
                // Gestione dell'errore
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                    Log.d("FirestoreError", "Error fetching document: " + e.getMessage());
                }
            }
        });
    }

    // Mappa per memorizzare il testo di ciascun EditText
    Map<Integer, String> editTextData = new HashMap<>();
    // Supponiamo di avere un ArrayList per tenere traccia degli ID eliminati
    ArrayList<Integer> deletedEditTextIds = new ArrayList<>();


    /**
     * Aggiunge un EditText e un bottone di eliminazione al layout.
     * L'utente può inserire testo, che viene poi salvato in Firestore.
     * Un ID univoco è assegnato all'EditText, che può essere riutilizzato dagli ID eliminati.
     * Un bottone di eliminazione consente di rimuovere l'EditText e l'ID corrispondente dall'elenco.
     *
     * @param userId L'identificativo unico dell'utente, utilizzato per salvare i dati in Firestore.
     */

    public void addEditTextAndDeleteButton(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Text");

        final EditText inputField = new EditText(getContext());
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputField);

        builder.setPositiveButton("OK",(dialog, which) -> {
            EditText editText = new EditText(getContext());
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            editText.setTextColor(Color.BLACK); // Imposta il colore del testo a nero

            editText.setText(inputField.getText().toString());

            int newEditTextId;

            if (!deletedEditTextIds.isEmpty()) {
                // Se ci sono ID liberi, usa il primo ID disponibile
                newEditTextId = deletedEditTextIds.remove(0); // Rimuove e restituisce il primo elemento

                // Aggiorna Firestore con la lista aggiornata di ID eliminati
                updateFireStoreWithIdsDeleted(userId);
            } else {
                // Se non ci sono ID liberi, incrementa il contatore e usa il nuovo ID
                countEditText++;
                newEditTextId = countEditText;
            }

            editText.setId(newEditTextId);
            editTextData.put(newEditTextId, inputField.getText().toString());

            editTextMap.put(editText.getId(), editText);

            // Aggiungi un TextWatcher per tracciare i cambiamenti
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Implementazione non necessaria
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editTextData.put(editText.getId(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Implementazione non necessaria
                }
            });

            Button deleteButton = new Button(getContext());
            deleteButton.setText(R.string.delete);
            deleteButton.setId(countEditText + 1000); // Assicurati che l'ID del bottone sia univoco

            // Aggiungi listener al bottone di eliminazione
            deleteButton.setOnClickListener(v -> {

                int deletedId = editText.getId(); // Ottieni l'ID dell'EditText eliminato
                container.removeView(editText);
                container.removeView(deleteButton);
                deletedEditTextIds.add(deletedId);
                editTextMap.remove(editText.getId());

                updateFireStoreWithIdsDeleted(userId);
            });

            container.addView(editText);
            container.addView(deleteButton);

            Map<String, Object> note = new HashMap<>();
            note.put(String.valueOf(newEditTextId), inputField.getText().toString());
            note.put(KEY_NUMBER_EDIT_TEXT, countEditText);

            db.collection("Pathologies").document(userId)
                    .update(note)
                    .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "User data saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Aggiorna Firestore con l'elenco degli ID degli EditText eliminati.
     * Questo metodo è chiamato dopo l'eliminazione di un EditText per mantenere aggiornato l'elenco degli ID disponibili.
     *
     * @param userId L'identificativo unico dell'utente, usato per identificare il documento Firestore corretto.
     */

    public void updateFireStoreWithIdsDeleted(String userId){
        // Aggiorna Firestore con l'ID eliminato
        Map<String, Object> updates = new HashMap<>();
        updates.put(KEY_IDS_DELETED, deletedEditTextIds); // Aggiunge l'ID all'array 'deleted_ids'

        db.collection("Pathologies").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID eliminato aggiornato con successo"))
                .addOnFailureListener(e -> Log.w("Firestore", "Errore nell'aggiornamento dell'ID eliminato", e));

    }

    /**
     * Salva i dati dell'utente in Firestore.
     * [Dettagli aggiuntivi non disponibili senza il codice della funzione.]
     *
     * @param userId L'identificativo unico dell'utente, utilizzato per identificare il documento Firestore in cui salvare i dati.
     */

    public void saveData(String userId) {
        Map<String, Object> note = new HashMap<>();
        for (Map.Entry<Integer, String> entry : editTextData.entrySet()) {
            note.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        db.collection("Pathologies").document(userId)
                .update(note)
                .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Modifications saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error saving modifications!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Disabilita ricorsivamente tutte le View all'interno di un ViewGroup.
     * Questa funzione attraversa tutte le View contenute in un ViewGroup specificato e le disabilita,
     * rendendo l'interfaccia utente non interattiva. Se una View è anch'essa un ViewGroup,
     * la funzione viene applicata ricorsivamente a quel ViewGroup.
     *
     * @param layout Il ViewGroup il cui contenuto deve essere disabilitato.
     */
    private void disableAllViews(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(false); // Disabilita la View
            if (child instanceof ViewGroup) {
                disableAllViews((ViewGroup) child); // Ricorsione per sottolayout
            }
        }
    }

    /**
     * Abilita ricorsivamente tutte le View all'interno di un ViewGroup.
     * Questa funzione attraversa tutte le View contenute in un ViewGroup specificato e le abilita,
     * rendendo l'interfaccia utente interattiva di nuovo. Se una View è anch'essa un ViewGroup,
     * la funzione viene applicata ricorsivamente a quel ViewGroup.
     *
     * @param layout Il ViewGroup il cui contenuto deve essere abilitato.
     */
    private void enableAllViews(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(true); // Abilita la View
            if (child instanceof ViewGroup) {
                enableAllViews((ViewGroup) child); // Ricorsione per sottolayout
            }
        }
    }

    /**
     * Salva i nuovi dati dell'utente su Firestore.
     * @param userId L'ID dell'utente per il quale salvare i dati.
     */
    public void saveNewLifeStyleUser(String userId){
        //Prendo e salvo il testo degli EditText
        String medicines = eMedicines.getText().toString();
        String drugs = eDrugs.getText().toString();
        String water = eDrinkWater.getText().toString();
        String drink = eDrink.getText().toString();
        String vegetablesAndFruit = eEatVegetablesAndFruit.getText().toString();
        String exercise = eExercise.getText().toString();
        String goodDiet = eGoodDiet.getText().toString();
        String allergies = eAllergies.getText().toString();
        String sleep = eSleep.getText().toString();
        String smoke = eSmoke.getText().toString();
        String familyDiseases = eFamilyDiseases.getText().toString();
        String operated = eOperated.getText().toString();
        String meal = eMealsADay.getText().toString();
        String dietOn = eDietOn.getText().toString();

        Map<String, Object> note = new HashMap<>();

        //Controllo quali campi sono vuoti e quali no
        if (!medicines.isEmpty()) {
            note.put(KEY_MEDICINES, medicines);
        }
        if (!drugs.isEmpty()) {
            note.put(KEY_DRUGS, drugs);
        }
        if (!water.isEmpty()) {
            note.put(KEY_WATER, water);
        }
        if (!drink.isEmpty()) {
            note.put(KEY_DRINK, drink);
        }
        if (!vegetablesAndFruit.isEmpty()){
            note.put(KEY_VEGETABLES_FRUIT, vegetablesAndFruit);
        }
        if (!exercise.isEmpty()) {
            note.put(KEY_EXERCISE, exercise);
        }
        if (!goodDiet.isEmpty()) {
            note.put(KEY_GOOD_DIET, goodDiet);
        }
        if (!allergies.isEmpty()) {
            note.put(KEY_ALLERGIES, allergies);
        }
        if (!sleep.isEmpty()) {
            note.put(KEY_SLEEP, sleep);
        }
        if (!smoke.isEmpty()) {
            note.put(KEY_SMOKE, smoke);
        }
        if (!familyDiseases.isEmpty()) {
            note.put(KEY_FAMILY_DISEASES, familyDiseases);
        }
        if (!operated.isEmpty()) {
            note.put(KEY_OPERATED, operated);
        }
        if (!meal.isEmpty()) {
            note.put(KEY_MEALS_EAT_DAY, meal);
        }
        if (!dietOn.isEmpty()) {
            note.put(KEY_DIET_ON, dietOn);
        }

        // Aggiorna solo i campi non vuoti
        if (!note.isEmpty()) {
            db.collection("Lifestyle Users")
                    .document(userId + " Lifestyle").set(note, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && getActivity() != null) {
                            Toast.makeText(getActivity(), "Changes saved!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Condivide le informazioni sullo stile di vita dell'utente tramite WhatsApp.
     * Raccoglie dettagli riguardanti vari aspetti dello stile di vita, come uso di medicinali, droghe,
     * consumo di acqua, alcol, frutta e verdura, attività fisica, qualità della dieta, allergie,
     * abitudini di sonno, fumo, malattie familiari, interventi chirurgici subiti, numero di pasti al giorno,
     * e aderenza alla dieta. Questi dati sono raccolti da una serie di EditText e poi concatenati
     * in una singola stringa. La stringa viene poi inviata ad WhatsApp tramite un Intent.
     * Se WhatsApp non è installato, viene mostrato all'utente un messaggio di errore.
     */
    public void shareLifeStyleUser(){
        // Concatena il testo
        String data = KEY_MEDICINES + " " + eMedicines.getText().toString() + "\n" +
                KEY_DRUGS + " " + eDrugs.getText().toString() + "\n" +
                KEY_WATER + " " + eDrinkWater.getText().toString() + "\n" +
                KEY_DRINK + " " + eDrink.getText().toString() + "\n" +
                KEY_VEGETABLES_FRUIT + " " + eEatVegetablesAndFruit.getText().toString() + "\n" +
                KEY_EXERCISE + " " + eExercise.getText().toString() + "\n" +
                KEY_GOOD_DIET + " " + eGoodDiet.getText().toString() + "\n" +
                KEY_ALLERGIES + " " + eAllergies.getText().toString() + "\n" +
                KEY_SLEEP + " " + eSleep.getText().toString() + "\n" +
                KEY_SMOKE + " " + eSmoke.getText().toString() + "\n" +
                KEY_FAMILY_DISEASES + " " + eFamilyDiseases.getText().toString() + "\n" +
                KEY_OPERATED + " " + eOperated.getText().toString() + "\n" +
                KEY_MEALS_EAT_DAY + " " + eMealsADay.getText().toString() + "\n" +
                KEY_DIET_ON + " " + eDietOn.getText().toString() + "\n";

        // Crea e configura l'Intent per la condivisione
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Condivide le misurazioni di salute dell'utente tramite WhatsApp.
     * Raccoglie dati come frequenza cardiaca, pressione, diabete, frequenza respiratoria,
     * ossigenazione e temperatura corporea dagli EditText corrispondenti e li concatena in un singolo String.
     * Invia questa stringa tramite un Intent ad WhatsApp. Se WhatsApp non è installato,
     * mostra un messaggio di errore all'utente.
     */
    public void shareMeasurement(){
        // Concatena il testo
        String measurement = "Heartbeat rate: " + tHeartData.getText().toString() + " Date: " + tDateHeart.getText().toString() + "\n" +
                "Pressure: " + tPressureData.getText().toString() + " Date: " + tDatePressure.getText().toString() + "\n" +
                "Diabetes: " + tDiabetesData.getText().toString() + " Date: " + tDateDiabetes.getText().toString() + "\n" +
                "Respiratory rate: " + tRespiratoryData.getText().toString() + "Date: " + tDateRespiratory.getText().toString() + "\n" +
                "Oxygenation: " + tOxygenationData.getText().toString() + " Date: " + tDateOxygenation.getText().toString() + "\n" +
                "Body temperature: " + tTemperatureData.getText().toString() + " Date: " + tDateTemperature.getText().toString() + "\n";

        // Crea e configura l'Intent per la condivisione
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, measurement);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Condivide le patologie dell'utente tramite WhatsApp.
     * Raccoglie i testi da un insieme di EditText, rappresentanti diverse patologie,
     * e li concatena in un unico String. Invia questa stringa tramite un Intent ad WhatsApp.
     * Se WhatsApp non è installato, mostra un messaggio di errore all'utente.
     */
    public void sharePathologies(){
        StringBuilder pathologiesBuilder = new StringBuilder();

        // Concatena il testo di tutti gli EditText
        for (EditText editText : editTextMap.values()) {
            String text = editText.getText().toString();
            if (!text.isEmpty()) {
                pathologiesBuilder.append(text).append("\n"); // Aggiungi una nuova linea tra ogni testo
            }
        }

        String pathologies = pathologiesBuilder.toString();

        // Crea e configura l'Intent per la condivisione
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, pathologies);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}