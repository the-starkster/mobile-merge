package com.example.asilapp10.maps.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class NearbySearch {

    public static String makePostDataPharmacy(int maxResultCount, LatLng position) {
        Gson gson = new Gson();

        // Create a JsonObject
        JsonObject jsonPostData = new JsonObject();

        JsonArray jsonTypes = new JsonArray();
        jsonTypes.add("pharmacy");
        jsonPostData.add("includedTypes", jsonTypes);

        jsonPostData.addProperty("languageCode", "it");
        jsonPostData.addProperty("maxResultCount", maxResultCount);

        JsonObject latlong = new JsonObject();
        latlong.addProperty("latitude", position.latitude);
        latlong.addProperty("longitude", position.longitude);

        JsonObject center = new JsonObject();
        center.add("center", latlong);
        center.addProperty("radius", 2000.0);

        JsonObject circle = new JsonObject();
        circle.add("circle", center);

        jsonPostData.add("locationRestriction", circle);


        // Convert the JsonObject to a JSON string
        return gson.toJson(jsonPostData);
    }

    public static String makePostDataReceptionCenter(int maxResultCount, LatLng position) {
        Gson gson = new Gson();

        // Create a JsonObject
        JsonObject jsonPostData = new JsonObject();

        jsonPostData.addProperty("textQuery", "Centro di accoglienza");
        jsonPostData.addProperty("languageCode", "it");
        jsonPostData.addProperty("maxResultCount", maxResultCount);

        JsonObject latlong = new JsonObject();
        latlong.addProperty("latitude", position.latitude);
        latlong.addProperty("longitude", position.longitude);

        JsonObject center = new JsonObject();
        center.add("center", latlong);
        center.addProperty("radius", 500.0);

        JsonObject circle = new JsonObject();
        circle.add("circle", center);

        jsonPostData.add("locationBias", circle);


        // Convert the JsonObject to a JSON string
        return gson.toJson(jsonPostData);
    }

    public static List<Place> extractDataFromResponse(String data, Place.PlaceType type) {
        List<Place> pharmacies = new ArrayList<>();

        JsonObject dataJSON = JsonParser.parseString(data).getAsJsonObject();
        JsonArray places = dataJSON.get("places").getAsJsonArray();
        System.out.println(places);

        for (JsonElement e : places) {
            //----Place----
            String name;
            LatLng coordinates;
            String address;
            Boolean openNow = null;
            List<String> currentOpeningHours = new ArrayList<>();
            float rating = 0F;
            int ratingCount = 0;
            //---------------

            JsonObject place = e.getAsJsonObject();
            name = place.get("displayName").getAsJsonObject().get("text").getAsString();//estraggo il nome
            coordinates = new LatLng(place.get("location").getAsJsonObject().get("latitude").getAsDouble(), place.get("location").getAsJsonObject().get("longitude").getAsDouble());// estraggo le coordinate come oggetto LatLng
            address = place.get("shortFormattedAddress").getAsString();//estraggo l'indirizzo

            if (place.has("rating")) { // If Pharmacy has "rating"
                Log.i("RATING", place.get("rating").getAsString());
                rating = place.get("rating").getAsFloat();
                ratingCount = place.get("userRatingCount").getAsInt();
            }

            if (place.has("currentOpeningHours")) { // If Pharmacy has "currentOpeningHours"

                JsonArray weekdayDescriptions = place.get("currentOpeningHours").getAsJsonObject().get("weekdayDescriptions").getAsJsonArray();
                for (JsonElement h : weekdayDescriptions) {
                    currentOpeningHours.add(h.getAsString());
                }

                // If Pharmacy has "openNow", extract it
                if (place.get("currentOpeningHours").getAsJsonObject().has("openNow")) {
                    openNow = place.get("currentOpeningHours").getAsJsonObject().get("openNow").getAsBoolean();
                }

            }

            // New istance added with extracted data
            pharmacies.add(new Place(name, type, coordinates, address, formatOpenNow(openNow), formatCurrentOpeningHours(currentOpeningHours), rating, formatRatingText(rating, ratingCount)));
        }

        //System.out.println(pharmacies);
        return pharmacies;
    }

    private static String formatRatingText(float rating, int ratingCount) {
        if (ratingCount == 0) {
            return "Nessuna valutazione";
        }
        return rating + " (" + ratingCount + ")";
    }

    private static String formatCurrentOpeningHours(List<String> currentOpeningHours) {

        if (currentOpeningHours.isEmpty()) {
            return "Nessuna informazione sugli orari d'apertura";
        } else {
            StringBuilder currentOpeningHoursString = new StringBuilder();
            for (String hour : currentOpeningHours) currentOpeningHoursString.append(hour).append("\n");
            currentOpeningHoursString.deleteCharAt(currentOpeningHoursString.lastIndexOf("\n")); // Remove last newline
            return currentOpeningHoursString.toString();
        }

    }

    private static String formatOpenNow(Boolean openNow) {
        if (openNow == null) {
            return "Nessuna informazione sullo stato d'apertura";
        } else if (openNow) {
            return "Aperto";
        } else {
            return "Chiuso";
        }
    }

}
