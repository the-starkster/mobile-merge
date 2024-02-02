package com.example.asilapp10;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.asilapp10.QRCode.QRCodeBottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FragmentUserData extends Fragment {

    // Dichiarazione delle variabili per componenti dell'interfaccia utente
    TextView tfName, tlName, tCF, tbPlace, tdBirth, tNationality, tpResidence, tAddress, tpNumber, tEmail;
    Button buttonEdit, buttonApplyEdit, buttonLogout, buttonBack, buttonShare;
    FirebaseUser user;
    FirebaseAuth mAuth;

    String taxIDcode = "";
    String displayName = "";

    // Chiavi costanti per i campi nel database
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String KEY_FIRST_NAME = "First name";
    public static final String KEY_LAST_NAME = "Last name";
    public static final String KEY_TAX_ID_CODE = "Tax ID Code";
    public static final String KEY_BIRTH_PLACE = "Birth place";
    public static final String KEY_NATIONALITY = "Nationality";
    public static final String KEY_PLACE_OF_RESIDENCE = "Place of residence";
    public static final String KEY_ADDRESS = "Address";
    public static final String KEY_PHONE_NUMBER = "Phone number";
    public static final String KEY_DATE_BIRTH = "Date of birth";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inizializzazione dei componenti dell'interfaccia utente
        tfName = requireView().findViewById(R.id.tf_name);
        tlName = requireView().findViewById(R.id.tl_name);
        tCF = requireView().findViewById(R.id.tCF);
        tbPlace = requireView().findViewById(R.id.tb_place);
        tdBirth = requireView().findViewById(R.id.td_birth);
        tNationality = requireView().findViewById(R.id.t_nationality);
        tpResidence = requireView().findViewById(R.id.tp_residence);
        tAddress = requireView().findViewById(R.id.t_address);
        tpNumber = requireView().findViewById(R.id.tp_number);
        tEmail = requireView().findViewById(R.id.t_email);

        buttonEdit = requireView().findViewById(R.id.btn_edit_data);
        buttonApplyEdit = requireView().findViewById(R.id.btn_apply_edit_data);
        buttonLogout = requireView().findViewById(R.id.btn_logout);
        buttonBack = requireView().findViewById(R.id.btn_back_edit);
        buttonShare = requireView().findViewById(R.id.btn_share);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        String userId = (user != null) ? user.getUid() : "";
        queryFireStore(userId);

        //QRCode BottomSheet setup
        Button qrcodeButton = view.findViewById(R.id.QRCodeButton);
        qrcodeButton.setOnClickListener(v -> {
            QRCodeBottomSheetDialogFragment QRCodeButton = new QRCodeBottomSheetDialogFragment(displayName, taxIDcode);
            QRCodeButton.show(getChildFragmentManager(), QRCodeButton.getTag());

        });

        // Imposta il listener per il pulsante 'Edit'
        buttonEdit.setOnClickListener(v -> {

            // Abilita i campi di testo per la modifica
            tfName.setEnabled(true);
            tlName.setEnabled(true);
            tCF.setEnabled(true);
            tbPlace.setEnabled(true);
            tdBirth.setEnabled(true);
            tNationality.setEnabled(true);
            tpResidence.setEnabled(true);
            tAddress.setEnabled(true);
            tpNumber.setEnabled(true);

            // Cambia la visibilità dei pulsanti per l'editing
            buttonEdit.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.GONE);
            buttonShare.setVisibility(View.GONE);

            // Mostra i pulsanti 'Back' e 'Apply Edit'
            buttonBack.setVisibility(View.VISIBLE);
            buttonApplyEdit.setVisibility(View.VISIBLE);
        });

        // Imposta il listener per il pulsante 'Logout'
        buttonLogout.setOnClickListener(v -> {
            // Ottiene l'utente attualmente autenticato
            if (user != null && user.getEmail() != null && user.getEmail().equals("buono@gmail.com")) {
                // Cancella l'account se l'email corrisponde
                user.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Account cancellato con successo, reindirizza alla schermata di login
                        Intent intent = new Intent(getActivity(), Login.class);
                        startActivity(intent);

                        // Chiude l'activity corrente
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.finish();
                        }
                    }
                });
            } else {
                // Esegue il logout se l'email non corrisponde o l'utente non è autenticato
                FirebaseAuth.getInstance().signOut();

                // Reindirizza l'utente alla schermata di login
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);

                // Chiude l'activity corrente
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

        // Imposta il listener per il pulsante 'Apply Edit'
        buttonApplyEdit.setOnClickListener(v -> {

            // Disabilita i campi di testo dopo la modifica
            tfName.setEnabled(false);
            tlName.setEnabled(false);
            tCF.setEnabled(false);
            tbPlace.setEnabled(false);
            tdBirth.setEnabled(false);
            tNationality.setEnabled(false);
            tpResidence.setEnabled(false);
            tAddress.setEnabled(false);
            tpNumber.setEnabled(false);

            // Mostra i pulsanti 'Edit', 'Logout' e 'Share'
            buttonEdit.setVisibility(View.VISIBLE);
            buttonLogout.setVisibility(View.VISIBLE);
            buttonShare.setVisibility(View.VISIBLE);

            // Nasconde i pulsanti 'Back' e 'Apply Edit'
            buttonBack.setVisibility(View.GONE);
            buttonApplyEdit.setVisibility(View.GONE);

            // Salva i nuovi dati dell'utente
            saveNewDataUser(userId);
        });

        // Imposta il listener per il pulsante 'Back'
        buttonBack.setOnClickListener(v -> {

            // Disabilita i campi di testo
            tfName.setEnabled(false);
            tlName.setEnabled(false);
            tCF.setEnabled(false);
            tbPlace.setEnabled(false);
            tdBirth.setEnabled(false);
            tNationality.setEnabled(false);
            tpResidence.setEnabled(false);
            tAddress.setEnabled(false);
            tpNumber.setEnabled(false);

            // Mostra i pulsanti 'Edit', 'Logout' e 'Share'
            buttonEdit.setVisibility(View.VISIBLE);
            buttonLogout.setVisibility(View.VISIBLE);
            buttonShare.setVisibility(View.VISIBLE);

            // Nasconde i pulsanti 'Back' e 'Apply Edit'
            buttonBack.setVisibility(View.GONE);
            buttonApplyEdit.setVisibility(View.GONE);
        });

        // Imposta il listener per il pulsante 'Share'
        buttonShare.setOnClickListener(v -> {

            // Crea un AlertDialog per confermare la condivisione
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.confirm_sharing));
            builder.setMessage(getString(R.string.share_personal_data));

            // Imposta i pulsanti di risposta nell'AlertDialog
            builder.setPositiveButton(getString(R.string.yes), (dialog, id) -> shareDataUser());
            builder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());

            // Mostra l'AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    /**
     * Interroga Firestore per ottenere i dati dell'utente.
     * @param userId L'ID dell'utente per il quale recuperare i dati.
     */
    private void queryFireStore(String userId) {

        DocumentReference docRef = db.collection("Users Data")
                .document(userId + " Personal Data");

        // Effettua la interrogazione
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("FirestoreDocument", "Document Snapshot: " + documentSnapshot.getData());

                    // Imposta i dati sulle TextView
                    tfName.setText(documentSnapshot.getString("First name"));
                    tlName.setText(documentSnapshot.getString("Last name"));
                    tCF.setText(documentSnapshot.getString("Tax ID Code"));
                    tbPlace.setText(documentSnapshot.getString("Birth place"));
                    tdBirth.setText(documentSnapshot.getString("Date of birth"));
                    tNationality.setText(documentSnapshot.getString("Nationality"));
                    tpResidence.setText(documentSnapshot.getString("Place of residence"));
                    tAddress.setText(documentSnapshot.getString("Address"));
                    tpNumber.setText(documentSnapshot.getString("Phone number"));
                    tEmail.setText(user.getEmail());

                    //Set displayName and taxIDcode for QRCode BottomSheet
                    displayName = documentSnapshot.getString(KEY_FIRST_NAME) + " " + documentSnapshot.getString(KEY_LAST_NAME);
                    taxIDcode = documentSnapshot.getString(KEY_TAX_ID_CODE);

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

    /**
     * Salva i nuovi dati dell'utente su Firestore.
     * @param userId L'ID dell'utente per il quale salvare i dati.
     */
    public void saveNewDataUser(String userId){
        String firstName = tfName.getText().toString();
        String lastName = tlName.getText().toString();
        String taxIdCode = tCF.getText().toString();
        String birthPlace = tbPlace.getText().toString();
        String dateBirth = tdBirth.getText().toString();
        String nationality = tNationality.getText().toString();
        String placeOfResidence = tpResidence.getText().toString();
        String address = tAddress.getText().toString();
        String phoneNumber = tpNumber.getText().toString();

        Map<String, Object> note = new HashMap<>();

        if (!firstName.isEmpty()) {
            note.put(KEY_FIRST_NAME, firstName);
        }
        if (!lastName.isEmpty()) {
            note.put(KEY_LAST_NAME, lastName);
        }
        if (!taxIdCode.isEmpty()) {
            note.put(KEY_TAX_ID_CODE, taxIdCode);
        }
        if (!birthPlace.isEmpty()) {
            note.put(KEY_BIRTH_PLACE, birthPlace);
        }
        if (!dateBirth.isEmpty()){
            note.put(KEY_DATE_BIRTH, dateBirth);
        }
        if (!nationality.isEmpty()) {
            note.put(KEY_NATIONALITY, nationality);
        }
        if (!placeOfResidence.isEmpty()) {
            note.put(KEY_PLACE_OF_RESIDENCE, placeOfResidence);
        }
        if (!address.isEmpty()) {
            note.put(KEY_ADDRESS, address);
        }
        if (!phoneNumber.isEmpty()) {
            note.put(KEY_PHONE_NUMBER, phoneNumber);
        }

        // Aggiorna solo i campi non vuoti
        if (!note.isEmpty()) {
            db.collection("Users Data")
                    .document(userId + " Personal Data").set(note, SetOptions.merge())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && getActivity() != null) {
                            Toast.makeText(getActivity(), "Changes saved!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Condivide i dati dell'utente tramite un'app di messaggistica.
     */
    public void shareDataUser(){

        String data = "Name: " + tfName.getText().toString() + "\n" +
                "Last name: " + tlName.getText().toString() + "\n" +
                "Tax ID Code: " + tCF.getText().toString() + "\n" +
                "Birth place: " + tbPlace.getText().toString() + "\n" +
                "Date of Birth: " + tdBirth.getText().toString() + "\n" +
                "Nationality: " + tNationality.getText().toString() + "\n" +
                "Place of residence: " + tpResidence.getText().toString() + "\n" +
                "Address: " + tAddress.getText().toString() + "\n" +
                "Phone number: " + tpNumber.getText().toString() + "\n" +
                "Email: " + tEmail.getText().toString() + "\n";

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
}