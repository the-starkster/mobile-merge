package com.example.asilapp10.QRCode;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.asilapp10.QRCode.utils.QRCode;
import com.example.asilapp10.R;

public class QRCodeFragment extends Fragment {

    private QRCodeViewModel mViewModel;

    public static QRCodeFragment newInstance() {
        return new QRCodeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        QRCode.Generate(view);//Genero e visualizzo il QRCode

    }

}