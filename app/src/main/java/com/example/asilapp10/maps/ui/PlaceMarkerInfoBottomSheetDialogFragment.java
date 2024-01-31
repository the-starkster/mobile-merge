package com.example.asilapp10.maps.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.asilapp10.R;
import com.example.asilapp10.maps.utils.Place;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class PlaceMarkerInfoBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private final Place mPlace;

    public PlaceMarkerInfoBottomSheetDialogFragment(Place p) {
        this.mPlace = p;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.PlaceMarkerBottomSheetDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_marker_info_bottom_sheet_dialog, container, false);

        //setting up close button
        ImageView closeButton = view.findViewById(R.id.bottomSheetCloseButton);
        closeButton.setOnClickListener(v -> dismiss());


        //showing all infos
        TextView mName = view.findViewById(R.id.PlaceName);
        TextView mAddress = view.findViewById(R.id.PlaceAddress);
        TextView mOpenNow = view.findViewById(R.id.PlaceOpenNow);
        TextView mCurrentOpeningHours = view.findViewById(R.id.PlaceCurrentOpeningHours);
        RatingBar mRatingBar = view.findViewById(R.id.PlaceRatingBar);
        TextView mRatingText = view.findViewById(R.id.PlaceRatingText);



        mName.setText(mPlace.getName());
        mAddress.setText(mPlace.getAddress());
        setOpenNow(mOpenNow);
        mCurrentOpeningHours.setText(mPlace.getCurrentOpeningHours());
        mRatingBar.setRating(mPlace.getRating());
        mRatingText.setText(mPlace.getRatingText());


        //button GoogleMaps Intent
        Button googleMapsIntentButton = view.findViewById(R.id.google_maps_intent_button);

        // Set an OnClickListener for the button
        googleMapsIntentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mapUri = Uri.parse("geo:" + mPlace.getCoordinates().latitude + "," + mPlace.getCoordinates().longitude + "?q=" + mPlace.getName());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                startActivity(mapIntent);
            }
        });



        return view;
    }

    private void setOpenNow(TextView mOpenNow) {
        if (Objects.equals(mPlace.isOpenNow(), "Aperto")) {
            mOpenNow.setText(mPlace.isOpenNow());
            mOpenNow.setTextColor(Color.rgb(25, 126, 57));
        }
        else if (Objects.equals(mPlace.isOpenNow(), "Chiuso")) {
            mOpenNow.setText(mPlace.isOpenNow());
            mOpenNow.setTextColor(Color.rgb(211, 49, 39));
        }
        else {
            mOpenNow.setText(mPlace.isOpenNow());
        }
    }
}
