package com.example.asilapp10.QRCode;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.asilapp10.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class QRCodeCore {
    public static void Generate(View view, String data) {
        ImageView qrCodeImageView = view.findViewById(R.id.qrCodeImageView);

        // Generate QR Code
        Bitmap qrCodeBitmap = generateQRCodeBitmap(data, 1000, 1000);

        // Display QR Code in ImageView
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
    }

    private static Bitmap generateQRCodeBitmap(String data, int width, int height) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
