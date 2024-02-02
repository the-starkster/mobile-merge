package com.example.asilapp10.QRCode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.asilapp10.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QRCodeBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private final String displayName;
    private final String taxIDcode;

    public QRCodeBottomSheetDialogFragment(String displayName, String taxIDcode) {
        this.displayName = displayName;
        this.taxIDcode = taxIDcode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qrcode_bottom_sheet_dialog, container, false);

        QRCodeCore.Generate(view, taxIDcode);//Genero e visualizzo il QRCode

        ImageView closeButton = view.findViewById(R.id.bottomSheetCloseButton);
        closeButton.setOnClickListener(v -> dismiss());

        TextView nameTextView = view.findViewById(R.id.QRCodeNameTextView);
        nameTextView.setText(displayName);


        return view;
    }
}

