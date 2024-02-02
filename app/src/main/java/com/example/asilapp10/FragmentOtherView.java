package com.example.asilapp10;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.asilapp10.maps.MapsFragment;

public class FragmentOtherView extends Fragment {

    Button buttonFragmentUser, buttonFragmentMaps,buttonFragmentHealth,
            buttonVideo;

    public FragmentOtherView() {
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
        return inflater.inflate(R.layout.fragment_other_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonFragmentUser = requireView().findViewById(R.id.btn_profile);
        buttonFragmentHealth = requireView().findViewById(R.id.btn_health);
        buttonVideo = requireView().findViewById(R.id.btn_video);
        buttonFragmentMaps = requireView().findViewById((R.id.btn_map));

        // Configura il click listener per il pulsante del profilo utente
        view.findViewById(R.id.btn_profile).setOnClickListener(v -> navigateToFragment(new FragmentUserData()));

        // Configura il click listener per il pulsante della mappa
        view.findViewById(R.id.btn_map).setOnClickListener(v -> navigateToFragment(new MapsFragment()));

        // Configura il click listener per il pulsante del profilo salute
        view.findViewById(R.id.btn_health).setOnClickListener(v -> navigateToFragment(new FragmentHealthProfile()));

        // Configura il click listener per il pulsante del video
        view.findViewById(R.id.btn_video).setOnClickListener(v -> navigateToFragment(new FragmentVideo()));
    }

    private void navigateToFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}