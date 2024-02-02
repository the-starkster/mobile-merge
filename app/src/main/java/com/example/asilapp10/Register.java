package com.example.asilapp10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Register extends AppCompatActivity {

    // Dichiarazione delle variabili per gli elementi dell'interfaccia utente
    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName,
            editTextTaxIdCode,editTextBirthPlace, editTextNationality,
            editTextPlaceOfResidence, editTextAddress, editTextPhoneNumber;
    Button buttonReg, buttonGuest;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressBar progressBar;
    TextView textView;
    DatePicker dateOfBirth;

    // Dichiarazione di costanti utilizzate come chiavi per i dati
    private static final String TAG = "Register";
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
    public static final String KEY_IDS_DELETED = "IDs deleted";

    // Dichiarazione di oggetti RadioGroup per le scelte multiple
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private RadioGroup radioGroup3;
    private RadioGroup radioGroup4;
    private RadioGroup radioGroup5;
    private RadioGroup radioGroup6;
    private RadioGroup radioGroup7;
    private RadioGroup radioGroup8;
    private RadioGroup radioGroup9;
    private RadioGroup radioGroup10;
    private RadioGroup radioGroup11;
    private RadioGroup radioGroup12;
    private RadioGroup radioGroup13;
    private RadioGroup radioGroup14;

    // Dichiarazione di costanti chiave per i dati utente
    public static final String KEY_FIRST_NAME = "First name";
    public static final String KEY_LAST_NAME = "Last name";
    public static final String KEY_TAX_ID_CODE = "Tax ID Code";
    public static final String KEY_BIRTH_PLACE = "Birth place";
    public static final String KEY_DATE_OF_BIRTH = "Date of birth";
    public static final String KEY_NATIONALITY = "Nationality";
    public static final String KEY_PLACE_OF_RESIDENCE = "Place of residence";
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_PHONE_NUMBER = "Phone number";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        setContentView(R.layout.activity_register);

        Date currentDate = new Date();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Inizializza l'istanza di FirebaseAuth

        mAuth = FirebaseAuth.getInstance();

        // Inizializza le view e i pulsanti

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        buttonGuest = findViewById(R.id.btn_guest);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Inizializza gli oggetti RadioGroup

        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);
        radioGroup4 = findViewById(R.id.radioGroup4);
        radioGroup5 = findViewById(R.id.radioGroup5);
        radioGroup6 = findViewById(R.id.radioGroup6);
        radioGroup7 = findViewById(R.id.radioGroup7);
        radioGroup8 = findViewById(R.id.radioGroup8);
        radioGroup9 = findViewById(R.id.radioGroup9);
        radioGroup10 = findViewById(R.id.radioGroup10);
        radioGroup11 = findViewById(R.id.radioGroup11);
        radioGroup12 = findViewById(R.id.radioGroup12);
        radioGroup13 = findViewById(R.id.radioGroup13);
        radioGroup14 = findViewById(R.id.radioGroup14);

        // Inizializza le altre view e variabili

        editTextFirstName = findViewById(R.id.f_name);
        editTextLastName = findViewById(R.id.l_name);
        editTextTaxIdCode = findViewById(R.id.cf);
        editTextBirthPlace = findViewById(R.id.b_place);
        dateOfBirth = findViewById(R.id.d_birth_picker);
        editTextNationality = findViewById(R.id.nationality);
        editTextPlaceOfResidence = findViewById(R.id.p_residence);
        editTextAddress = findViewById(R.id.address);
        editTextPhoneNumber = findViewById(R.id.phone_number);

        // Gestisce il click sul link "Login Now"

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Gestisce il click sul pulsante di registrazione

        buttonReg.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString() : "";

            // Verifica se l'email e la password sono state inserite

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Effettua la registrazione tramite Firebase Authentication

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            // Se la registrazione è riuscita, reindirizza a MainActivity

                            user = mAuth.getCurrentUser();
                            String userId = (user != null) ? user.getUid() : "";

                            // Salva i dati dell'utente, dello stile di vita e della salute in Firestore

                            UsersLifestyleData(userId);
                            UserData(userId);
                            HealthData(userId);

                            List<Double> dbInitialValue = Arrays.asList(0.0, 0.0, 0.0);
                            Map<String, Object> note = new HashMap<>();
                            note.put(formattedDate, dbInitialValue);

                            db.collection("Chart Pie Data")
                                    .document(userId + " FMO")
                                    .set(note)
                                    .addOnSuccessListener(unused -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });

                            Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        buttonGuest.setOnClickListener(v ->{
            progressBar.setVisibility(View.VISIBLE);
            String email = "buono@gmail.com";
            String password = "000000";

            // Effettua la registrazione tramite Firebase Authentication

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            // Se la registrazione è riuscita, reindirizza a MainActivity

                            user = mAuth.getCurrentUser();
                            String userId = (user != null) ? user.getUid() : "";

                            // Salva i dati dell'utente, dello stile di vita e della salute in Firestore

                            UsersLifestyleData(userId);
                            UserData(userId);
                            HealthData(userId);

                            List<Double> dbInitialValue = Arrays.asList(0.0, 0.0, 0.0);
                            Map<String, Object> note = new HashMap<>();
                            note.put(formattedDate, dbInitialValue);

                            db.collection("Chart Pie Data")
                                    .document(userId + " FMO")
                                    .set(note)
                                    .addOnSuccessListener(unused -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });

                            Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            
                        } else {
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    /**
     * Questo metodo è utilizzato per raccogliere i dati relativi allo stile di vita dell'utente
     * e salvarli nel database Firebase Firestore.
     *
     * @param userId ID univoco dell'utente a cui associare i dati dello stile di vita.
     */

    public void UsersLifestyleData(String userId){

        // Ottenere i valori selezionati dalle opzioni radio relative allo stile di vita dell'utente

        String smoke = !getSelectedRadioValue(radioGroup1).isEmpty() ? getSelectedRadioValue(radioGroup1) : "Yes";
        String drink = !getSelectedRadioValue(radioGroup2).isEmpty() ? getSelectedRadioValue(radioGroup2) : "Yes";
        String drugs = !getSelectedRadioValue(radioGroup3).isEmpty() ? getSelectedRadioValue(radioGroup3) : "Yes";
        String medicines = !getSelectedRadioValue(radioGroup4).isEmpty() ? getSelectedRadioValue(radioGroup4) : "Yes";
        String operated = !getSelectedRadioValue(radioGroup5).isEmpty() ? getSelectedRadioValue(radioGroup5) : "Yes";
        String family_diseases = !getSelectedRadioValue(radioGroup6).isEmpty() ? getSelectedRadioValue(radioGroup6) : "Yes";
        String exercise = !getSelectedRadioValue(radioGroup7).isEmpty() ? getSelectedRadioValue(radioGroup7) : "Yes";
        String good_diet = !getSelectedRadioValue(radioGroup8).isEmpty() ? getSelectedRadioValue(radioGroup8) : "Yes";
        String sleep = !getSelectedRadioValue(radioGroup9).isEmpty() ? getSelectedRadioValue(radioGroup9) : "Yes";
        String allergies = !getSelectedRadioValue(radioGroup10).isEmpty() ? getSelectedRadioValue(radioGroup10) : "Yes";
        String water = !getSelectedRadioValue(radioGroup11).isEmpty() ? getSelectedRadioValue(radioGroup11) : "Yes";
        String meals_eat_day = !getSelectedRadioValue(radioGroup12).isEmpty() ? getSelectedRadioValue(radioGroup12) : "Yes";
        String vegetables_fruits = !getSelectedRadioValue(radioGroup13).isEmpty() ? getSelectedRadioValue(radioGroup13) : "Yes";
        String diet_on = !getSelectedRadioValue(radioGroup14).isEmpty() ? getSelectedRadioValue(radioGroup14) : "Yes";


        // Crea un dizionario (Map) contenente i dati dello stile di vita dell'utente

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_SMOKE, smoke);
        note.put(KEY_DRUGS, drugs);
        note.put(KEY_DRINK, drink);
        note.put(KEY_MEDICINES, medicines);
        note.put(KEY_OPERATED, operated);
        note.put(KEY_FAMILY_DISEASES, family_diseases);
        note.put(KEY_EXERCISE, exercise);
        note.put(KEY_GOOD_DIET, good_diet);
        note.put(KEY_SLEEP, sleep);
        note.put(KEY_ALLERGIES, allergies);
        note.put(KEY_WATER, water);
        note.put(KEY_MEALS_EAT_DAY, meals_eat_day);
        note.put(KEY_VEGETABLES_FRUIT, vegetables_fruits);
        note.put(KEY_DIET_ON, diet_on);

        // Salvataggio dei dati dello stile di vita dell'utente nel database Firebase Firestore

        db.collection("Lifestyle Users").document(userId + " Lifestyle").set(note)
                .addOnSuccessListener(unused -> {

                    // Notificare l'utente che i dati sono stati salvati con successo

                    Toast.makeText(Register.this, "Lifestyle data saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    // Gestire eventuali errori durante il salvataggio dei dati

                    Toast.makeText(Register.this, "Error!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                });

    }

    /**
     * Ottiene il valore selezionato da un RadioGroup.
     *
     * @param radioGroup Il RadioGroup da cui ottenere il valore selezionato.
     * @return Il testo del RadioButton selezionato o una stringa vuota se nessuno è selezionato.
     */

    private String getSelectedRadioValue(RadioGroup radioGroup) {

        // Ottiene il valore selezionato da un RadioGroup

        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        } else {
            return "";
        }
    }

    /**
     * Questo metodo è utilizzato per raccogliere i dati personali dell'utente
     * e salvarli nel database Firebase Firestore.
     *
     * @param userId ID univoco dell'utente a cui associare i dati personali.
     */
    public void UserData(String userId){

        // Ottenere il giorno, il mese e l'anno dalla data di nascita selezionata

        int day = dateOfBirth.getDayOfMonth();
        int month = dateOfBirth.getMonth() + 1; // L'indice del mese inizia da 0
        int year = dateOfBirth.getYear();

        // Ottenere i valori inseriti dall'utente per i dati personali

        String firstName = editTextFirstName.getText().toString().isEmpty() ? "Paolo" : editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString().isEmpty() ? "Buono" : editTextLastName.getText().toString();
        String taxIdCode = editTextTaxIdCode.getText().toString().isEmpty() ? "AAAAAA" : editTextTaxIdCode.getText().toString();
        String birthPlace = editTextBirthPlace.getText().toString().isEmpty() ? "Bari" : editTextBirthPlace.getText().toString();

        String dateOfBirth = day + "/" + month + "/" + year;

        String nationality = editTextNationality.getText().toString().isEmpty() ? "Italiana" : editTextNationality.getText().toString();
        String placeOfResidence = editTextPlaceOfResidence.getText().toString().isEmpty() ? "Bari" : editTextPlaceOfResidence.getText().toString();
        String address = editTextAddress.getText().toString().isEmpty() ? "Via Aldo" : editTextAddress.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString().isEmpty() ? "333333333" : editTextPhoneNumber.getText().toString();


        // Creare un dizionario (Map) contenente i dati personali dell'utente

        Map<String, Object> note =  new HashMap<>();

        note.put(KEY_FIRST_NAME, firstName);
        note.put(KEY_LAST_NAME, lastName);
        note.put(KEY_TAX_ID_CODE, taxIdCode);
        note.put(KEY_BIRTH_PLACE, birthPlace);
        note.put(KEY_DATE_OF_BIRTH, dateOfBirth);
        note.put(KEY_NATIONALITY, nationality);
        note.put(KEY_PLACE_OF_RESIDENCE, placeOfResidence);
        note.put(KEY_ADDRESS, address);
        note.put(KEY_PHONE_NUMBER, phoneNumber);

        // Salvare i dati personali dell'utente nel database Firebase Firestore

        db.collection("Users Data").document(userId + " Personal Data").set(note)
                .addOnSuccessListener(unused -> {

                    // Notificare l'utente che i dati sono stati salvati con successo

                    Toast.makeText(Register.this, "User data saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    // Gestire eventuali errori durante il salvataggio dei dati

                    Toast.makeText(Register.this, "Error!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                });
    }

    /**
     * Salva i dati di salute iniziali dell'utente nel database.
     *
     * @param userId ID dell'utente per identificare i dati di salute.
     */

    public void HealthData(String userId){

        // Creazione di un nuovo oggetto "note" per i dati di salute

        Map<String, Object> note =  new HashMap<>();

        // Impostazione dei dati di salute iniziali a "No Data"

        note.put(KEY_HEART, "No Data");
        note.put(KEY_PRESSURE, "No Data");
        note.put(KEY_DIABETES, "No Data");
        note.put(KEY_RESPIRATORY_RATE, "No Data");
        note.put(KEY_OXYGENATION, "No Data");
        note.put(KEY_TEMPERATURE, "No Data");
        note.put(KEY_HEARTBEAT_DATE, "No Data");
        note.put(KEY_RESPIRATORY_RATE_DATE, "No Data");
        note.put(KEY_DIABETES_DATE, "No Data");
        note.put(KEY_PRESSURE_DATE, "No Data");
        note.put(KEY_OXYGENATION_DATE, "No Data");
        note.put(KEY_TEMPERATURE_DATE, "No Data");

        // Salvataggio dei dati di salute nel documento "User Measurement Data" nel database

        db.collection("User Measurement Data").document(userId + " Health Data")
                .set(note)
                .addOnSuccessListener(unused -> {
                    // Successo: i dati di salute sono stati salvati con successo nel database
                });

        // Creazione di un nuovo oggetto "notePathologies" per i dati sulle patologie

        Map<String, Object> notePathologies = new HashMap<>();

        // Creazione di una lista vuota per gli ID delle patologie eliminate

        List<Integer> idsDelete = new ArrayList<>();

        // Impostazione dell'ID delle patologie eliminate e dei dati a "No Data"

        notePathologies.put(KEY_IDS_DELETED, idsDelete);

        for (int i = 1; i <= 30; i++){
            notePathologies.put(String.valueOf(i), "No Data");
        }

        // Impostazione del numero di campi di modifica dei dati a 0

        notePathologies.put(KEY_NUMBER_EDIT_TEXT, 0);

        // Salvataggio dei dati sulle patologie nel documento "Pathologies" nel database

        db.collection("Pathologies").document(userId)
                .set(notePathologies)
                .addOnSuccessListener(unused -> {
                    // Successo: i dati sulle patologie sono stati salvati con successo nel database
                });
    }
}
