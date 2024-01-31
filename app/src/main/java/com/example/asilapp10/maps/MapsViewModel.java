package com.example.asilapp10.maps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.asilapp10.maps.utils.NearbySearch;
import com.example.asilapp10.maps.utils.Place;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsViewModel extends ViewModel {
    //$$$$
    private final MutableLiveData<List<Place>> mPharmaciesData = new MutableLiveData<>();
    private final MutableLiveData<List<Place>> mReceptionCentersData = new MutableLiveData<>();

    public LiveData<List<Place>> getPharmaciesData() {
        return mPharmaciesData;
    }
    public LiveData<List<Place>> getReceptionCentersData() {
        return mReceptionCentersData;
    }

    //$$$

    public void getNearbyPlaces(Activity activity, int maxResultCount, LatLng position) throws IOException {
        getNearbyPharmacies(activity, maxResultCount, position);
        getNearbyReceptionCenters(activity, maxResultCount, position);
    }

    public void getNearbyPharmacies(Activity activity, int maxResultCount, LatLng position) {
        //makePostData
        String data = NearbySearch.makePostDataPharmacy(maxResultCount, position);

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(data, JSON);

        Request request = new Request.Builder()
                .url("https://places.googleapis.com/v1/places:searchNearby")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", "AIzaSyBMjkjquDd98LbCqYtgpU4P6aqZPc778DA")
                .addHeader("X-Goog-FieldMask", "places.displayName,places.location,places.currentOpeningHours,places.shortFormattedAddress,places.rating,places.userRatingCount")
                .post(body)
                .build();

        // Esegui la richiesta in modo asincrono
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Ottieni la risposta del server
                    assert response.body() != null;
                    String responseBody = response.body().string();

                    if (responseBody.contains("{}")) {
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Nessuna farmacia trovata!", Toast.LENGTH_SHORT).show());

                    } else {
                        List<Place> pharmacies = NearbySearch.extractDataFromResponse(responseBody, Place.PlaceType.PHARMACY);
                        activity.runOnUiThread(() -> mPharmaciesData.setValue(pharmacies));
                    }



                } else {
                    System.out.println("Errore nella risposta del server: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Gestisci eventuali errori di connessione o altre eccezioni
                e.printStackTrace();
            }
        });
    }

    public void getNearbyReceptionCenters(Activity activity, int maxResultCount, LatLng position) {
        //makePostData
        String data = NearbySearch.makePostDataReceptionCenter(maxResultCount, position);

        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(data, JSON);

        //set request for TextSearch
        Request request = new Request.Builder()
                .url("https://places.googleapis.com/v1/places:searchText")
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", "AIzaSyBMjkjquDd98LbCqYtgpU4P6aqZPc778DA")
                .addHeader("X-Goog-FieldMask", "places.displayName,places.location,places.currentOpeningHours,places.shortFormattedAddress,places.rating,places.userRatingCount")
                .post(body)
                .build();

        // Esegui la richiesta in modo asincrono
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Ottieni la risposta del server
                    assert response.body() != null;
                    String responseBody = response.body().string();

                    if (responseBody.contains("{}")) {
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Nessun centro di accoglienza trovato!", Toast.LENGTH_SHORT).show());

                    } else {
                        List<Place> receptionCenters = NearbySearch.extractDataFromResponse(responseBody, Place.PlaceType.RECEPTION_CENTER);
                        activity.runOnUiThread(() -> mReceptionCentersData.setValue(receptionCenters));
                    }

                } else {
                    System.out.println("Errore nella risposta del server: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Gestisci eventuali errori di connessione o altre eccezioni
                e.printStackTrace();
            }
        });
    }

    public BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector
        // drawable.
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
