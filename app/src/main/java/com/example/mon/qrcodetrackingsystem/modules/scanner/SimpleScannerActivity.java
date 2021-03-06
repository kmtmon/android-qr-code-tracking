package com.example.mon.qrcodetrackingsystem.modules.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemStatusManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.ConfirmDeliveryActivity;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.DeliveryListActivity;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.EditItemActivity;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.OnDeliveryListActivity;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    private String TAG = SimpleScannerActivity.class.getSimpleName();
    private ZXingScannerView mScannerView;
    private final static String IS_DELIVERY_OPTION = "IS_DELIVERY_OPTION";
    private final static String IS_ON_DELIVERY_OPTION = "IS_ON_DELIVERY_OPTION";

    //region Entry
    public static void show(Context context, boolean isDeliveryOption, boolean isOnDeliveryOption) {
        Intent intent = new Intent(context, SimpleScannerActivity.class);
        intent.putExtra(IS_DELIVERY_OPTION, isDeliveryOption);
        intent.putExtra(IS_ON_DELIVERY_OPTION, isOnDeliveryOption);
        context.startActivity(intent);
    }
    //endregion

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d(TAG, "Raw Text " + rawResult.getText()); // Prints scan results
        try {

            Map<String, Object> map = new HashMap<>();
            Gson gson = new Gson();
            map = (Map<String, Object>) gson.fromJson(rawResult.getText(), map.getClass());

            if (map.get("id") != null && !map.get("id").toString().isEmpty()) {
                mScannerView.stopCamera();
                ItemManager.getInstance().retrieveItem(map.get("id").toString(), item -> {
                    if (item.getId() != null) {
                        if (getIntent().getBooleanExtra(IS_DELIVERY_OPTION, false)) {

                            if(item.status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)){
                                finish();
                                SharedPreferenceManager.getInstance(this).storeDeliveryItem(item);
                                DeliveryListActivity.show(this);
                                return;
                            }

                        } else if (getIntent().getBooleanExtra(IS_ON_DELIVERY_OPTION, false)) {

                            if(item.status.equalsIgnoreCase(ItemStatusManager.OUT_FOR_DELIVERY)){
                                finish();
                                ConfirmDeliveryActivity.show(this, item.getId());
                                return;
                            }

                        }
                        finish();
                        EditItemActivity.show(this, item.getId());
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Cannot convert to object");
        }

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}
