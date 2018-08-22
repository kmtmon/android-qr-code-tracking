package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDeliveryListBinding;
import com.example.mon.qrcodetrackingsystem.manager.DeliveryManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemStatusManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Delivery;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DeliveryListAdapter;
import com.example.mon.qrcodetrackingsystem.modules.login.view.activity.LoginActivity;
import com.example.mon.qrcodetrackingsystem.modules.scanner.SimpleScannerActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;

public class DeliveryListActivity extends BaseActivity {

    //region Entry
    /**
     * Entry
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, DeliveryListActivity.class);
        context.startActivity(intent);
    }
    //endregion

    ActivityDeliveryListBinding mBinding;

    private String TAG = DeliveryListActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private DeliveryListAdapter mAdapter;
    private List<Item> mItemList = new ArrayList<>();

    @Override
    public void onBackPressed() {

        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setButtonsColorRes(R.color.red)
                .setMessage("Are you sure you want to go back? Current delivery list will be cleared")
                .setTopTitleColor(R.color.pure_white)
                .setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferenceManager.getInstance(DeliveryListActivity.this).removeAllDeliveryItemsAndProducts();
                        goback();
                    }
                })
                .show();
    }

    private void goback(){
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mItemList.clear();
        List<Item> newArray = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        mItemList.addAll(newArray);
        mItemList = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(DeliveryListActivity.this, R.layout.activity_delivery_list);
        mBinding.loading.setVisibility(View.VISIBLE);

        mItemList.clear();
        List<Item> newArray = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        mItemList.addAll(newArray);

        mRecyclerView = mBinding.recyclerView;
        setUpProductAdapter();
        mAdapter.notifyDataSetChanged();

        //region Click
        RxUtils.clicks(mBinding.confirm)
            .subscribe(view -> {
                createDeliveryLog();
                updateItemOutForDelivery();
                MapActivity.show(this);

            });

        RxUtils.clicks(mBinding.add)
            .subscribe(view -> {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    SimpleScannerActivity.show(this, true, false);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{CAMERA},
                            1);
                }
            });
        //endregion

    }


    private void updateItemOutForDelivery() {
        for (int i = 0; i < mItemList.size(); i++) {
            ItemManager.getInstance().updateItemDeliveryStatus(mItemList.get(i).getId(), ItemStatusManager.OUT_FOR_DELIVERY, "");
        }
    }

    private void createItemLog(String deliveryLogId) {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();

        for (int i = 0; i < mItemList.size(); i++) {
            long unixTime = System.currentTimeMillis() / 1000L;
            ItemLog log = ItemLogManager.getInstance().createNewItemLog(mItemList.get(i).getId(), currentUserId, unixTime, ItemStatusManager.OUT_FOR_DELIVERY, deliveryLogId);
            ItemLogManager.getInstance().saveItemLog(log);
        }
    }

    private void createDeliveryLog() {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
        long unixTime = System.currentTimeMillis() / 1000L;

        Delivery newDelivery = new Delivery();
        newDelivery.setUserId(currentUserId);
        newDelivery.setLat(0);
        newDelivery.setLng(0);
        newDelivery.setTimestamp(unixTime);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("delivery")
                .add(newDelivery)
                .addOnSuccessListener(documentReference ->
                        createItemLog(documentReference.getId()))
                .addOnFailureListener(e -> Log.w("", "Error adding document", e));
    }

    private void setUpProductAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeliveryListAdapter(mItemList, new DeliveryListAdapter.DeliveryListListener() {
            @Override
            public void clickOnItem(String itemId) {
                /** NOT USING*/
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mBinding.loading.setVisibility(View.GONE);
    }
}
