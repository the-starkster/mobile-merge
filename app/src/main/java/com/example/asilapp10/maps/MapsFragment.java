package com.example.asilapp10.maps;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.asilapp10.maps.ui.PlaceMarkerInfoBottomSheetDialogFragment;
import com.example.asilapp10.R;
import com.example.asilapp10.maps.utils.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsFragment extends Fragment implements OnMapReadyCallback, OnMapLoadedCallback {

    private Bundle savedState;
    private final Map<Place, String> mPlaces = new HashMap<>();
    private GoogleMap mMap;
    private MapsViewModel mViewModel;
    private final List<Marker> Pharmacy_MarkerList = new ArrayList<>();
    private final List<Marker> ReceptionCenter_MarkerList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        postponeEnterTransition();
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mViewModel = new ViewModelProvider(this).get(MapsViewModel.class);


        //----------------------AUTOCOMPLETE PLACES API FROM FAB-----------------------------------
        //Initialize Places
        String apiKey = "AIzaSyBMjkjquDd98LbCqYtgpU4P6aqZPc778DA";
        Places.initialize(requireContext(), apiKey);

        //Handling Autocomplete intent result
        final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {

                Intent intent = result.getData();
                if (intent != null) {

                    com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(intent);
                    try {
                        mViewModel.getNearbyPlaces(requireActivity(), 15, place.getLatLng());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Objects.requireNonNull(place.getLatLng()), 14), 1000, null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete");
            }
        });

        //Set return fields from Place (we want LAT_LNG in particular)
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

        FloatingActionButton fabAutocompleteSupportFragment = requireActivity().findViewById(R.id.fab_autocomplete_support_fragment);

        //Set click listener for the FloatingActionButton
        fabAutocompleteSupportFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform your action when FloatingActionButton is clicked
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireContext());
                startAutocomplete.launch(intent);
            }
        });
        //-----------------------------------------------------------------------------------------


        //----------------------------Retrieve CurrentLocation from FAB---------------------------
        //"onRequestPermissionsResult" for fragment is deprecated, this is the new method:
        ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {

            boolean permission = false;

            //Permissions request result is like "FINE_LOCATION -> true, COARSE LOCATION -> false"
            //Checking here if at least one is "true"
            for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                //Log.d("DEBUG", entry.getKey() + " = " + entry.getValue());
                if (entry.getValue()) {
                    permission = true;
                    break;
                }
            }

            //If at least one is "true"
            if (permission) {
                //Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
            else {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        });


        // Retrieve FAB for CurrentLocation
        FloatingActionButton fabCurrentLocation = requireActivity().findViewById(R.id.fab_current_location);
        // Set click listener for the FloatingActionButton
        fabCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First check permissions
                if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    //Requesting location permissions
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
                } else {
                    //Toast.makeText(requireContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
                    getCurrentLocation();
                }
            }
        });
        //-----------------------------------------------------------------------------------------
    }

    //In main activity we use .replace() that replaces a fragment in a container with an instance of a new fragment and onDestroyView() is called
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedState = new Bundle();
        savedState.putParcelable("CAMERA_POSITION", mMap.getCameraPosition());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);//callback triggered when map is full loaded

        // Hide myLocationButton because we'll use ours
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (savedState != null) {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Objects.requireNonNull(savedState.getParcelable("CAMERA_POSITION"))));
        } else {
            // Set default position to Italy
            LatLng italyNE = new LatLng(47.6419, 19.0374);
            LatLng italySW = new LatLng(35.7063, 5.3851);
            LatLngBounds italyBounds = new LatLngBounds(italySW, italyNE);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(italyBounds, 0));
        }

        // Observe changes in pharmacies data
        mViewModel.getPharmaciesData().observe(getViewLifecycleOwner(), pharmacies -> {
            removeMarkers(Pharmacy_MarkerList);
            addMarkers(pharmacies);
        });

        // Observe changes in receptionCenters data
        mViewModel.getReceptionCentersData().observe(getViewLifecycleOwner(), receptionCenters -> {
            removeMarkers(ReceptionCenter_MarkerList);
            addMarkers(receptionCenters);
        });

        // Set marker click listener
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // Retrieve Place associated to the marker
                Place p = null;
                for (Map.Entry<Place, String> entry : mPlaces.entrySet()) {
                    if (marker.getId().equals(entry.getValue())) {
                        p = entry.getKey();
                    }
                }

                // Adjust camera to show bottomSheetDialog properly
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude - 0.002, marker.getPosition().longitude), 16), 500, null);
                marker.showInfoWindow(); // Show place's name

                // New istance of BottomSheetFragment and then show up
                PlaceMarkerInfoBottomSheetDialogFragment PlaceMarkerButton = new PlaceMarkerInfoBottomSheetDialogFragment(p);
                PlaceMarkerButton.show(getChildFragmentManager(), PlaceMarkerButton.getTag());


                return true; // Consume the event
            }
        });
    }

    public void addMarkers(List<Place> places) {
        for (Place p : places) {

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(p.getCoordinates())
                    .title(p.getName())
            );

            if (p.getType().equals(Place.PlaceType.PHARMACY)) {
                assert marker != null;
                marker.setIcon(mViewModel.BitmapFromVector(requireContext(), R.drawable.marker_pharmacy));
                Pharmacy_MarkerList.add(marker);
            } else if (p.getType().equals(Place.PlaceType.RECEPTION_CENTER)) {
                assert marker != null;
                marker.setIcon(mViewModel.BitmapFromVector(requireContext(), R.drawable.marker_reception_center));
                ReceptionCenter_MarkerList.add(marker);
            }

            assert marker != null;
            mPlaces.put(p, marker.getId());
        }
    }

    // Function to remove all markers from the map
    private void removeMarkers(List<Marker> list) {
        for (Marker marker : list) {
            marker.remove();
        }
        list.clear();
    }

    // Triggered when map is full loaded
    @Override
    public void onMapLoaded() {
        startPostponedEnterTransition();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Check if GPS is enabled
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean mGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!mGPS) {
            Toast.makeText(requireContext(), "You should turn gps on", Toast.LENGTH_SHORT).show();
        }

        //Setting up LocationRequest to retrieve location updates
        LocationRequest mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 100)
                .setWaitForAccurateLocation(false)
                .setMaxUpdateDelayMillis(100)
                .setMaxUpdates(1)
                .build();

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());// Initialize fusedLocationClient
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mCurrentLocationCallback, Looper.myLooper());// Requesting location updates
    }

    LocationCallback mCurrentLocationCallback = new LocationCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationResult(LocationResult locationResult) {

            List<Location> locationList = locationResult.getLocations();
            if (!locationList.isEmpty()) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                //Get nearby pharmacies and reception centers around current location
                try {
                    removeMarkers(Pharmacy_MarkerList);
                    removeMarkers(ReceptionCenter_MarkerList);
                    mMap.setMyLocationEnabled(true);
                    mViewModel.getNearbyPlaces(requireActivity(), 10, new LatLng(location.getLatitude(), location.getLongitude()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14), 1000, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Toast.makeText(requireContext(), "locationList is empty (location update returned null)", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
