package com.example.asilapp10;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FragmentVideo extends Fragment {

    private static final String TAG = "FragmentVideo"; // Tag per i log
    VideoView videoView; // Definisco il videoView

    // Ottengo l'istanza di FirebaseStorage per poi ottenere il video tramire il suo pathString
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference videoRef = storageRef.child("WhatsApp Video 2024-01-26 at 13.54.04.mp4");

    public FragmentVideo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Called");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Called");


        //Ottengo il riferimento a VideoView
        videoView = view.findViewById(R.id.videoView);
        if (videoView == null) {
            Log.e(TAG, "onViewCreated: VideoView not found!");
        }

        getVideoFromFireStorage();
    }

    /**
     * Carica e riproduce un video da Firebase Storage.
     * Questo metodo richiede l'URL di download di un video memorizzato in Firebase Storage.
     * In caso di successo, il video viene impostato per essere riprodotto in un VideoView.
     * Sono inclusi anche un MediaController per il controllo della riproduzione e
     * listener per gestire gli stati di preparazione, completamento e errori del video.
     *
     * <p>Il metodo utilizza le espressioni lambda per gestire i callback di successo e fallimento
     * del processo di download.</p>
     *
     * <p>In caso di fallimento nel recupero dell'URL, viene registrato un errore nel log.</p>
     *
     * @throws IllegalArgumentException se l'URL del video non è valido o non può essere riprodotto.
     */
    public void getVideoFromFireStorage() {
        Log.d(TAG, "getVideoFromFireStorage: Called");
        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "Firebase onSuccess: Video URI is " + uri);
            videoView.setVideoURI(uri);

            // Aggiunta del MediaController
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            // Listener di Preparazione
            videoView.setOnPreparedListener(mp -> {
                Log.d(TAG, "VideoView onPrepared: Video is ready to play");
                videoView.start();
            });

            // Listener di Completamento
            videoView.setOnCompletionListener(mp -> Log.d(TAG, "VideoView onCompletion: Playback completed"));

            // Listener di Errore
            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "VideoView onError: Error in playback, what: " + what + " extra: " + extra);
                return true;
            });
        }).addOnFailureListener(exception -> Log.e(TAG, "Firebase onFailure: ", exception));
    }
}
