package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityItemQrBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ItemQRActivity extends Activity {

    //region Entry
    public static void show(Context context, String payload) {
        Intent intent = new Intent(context, ItemQRActivity.class);
        intent.putExtra(ItemQRActivity.QR_PAYLOAD, payload);
        context.startActivity(intent);
    }
    //endregion

    public static String QR_PAYLOAD = "QR_PAYLOAD";
    private String TAG = ItemQRActivity.class.getSimpleName();

    ActivityItemQrBinding mBinding;

    private String mQRPayload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ItemQRActivity.this, R.layout.activity_item_qr);

        mQRPayload = getIntent().getStringExtra(QR_PAYLOAD);
        Log.e(TAG,"QR Payload "+mQRPayload);

        generateQR();
    }

    private void generateQR(){

        if(mQRPayload == null || mQRPayload.isEmpty()){
            return;
        }

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(mQRPayload, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            mBinding.qrImage.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
