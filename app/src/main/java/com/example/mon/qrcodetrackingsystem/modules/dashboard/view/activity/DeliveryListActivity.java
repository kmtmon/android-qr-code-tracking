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
import android.view.View;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDeliveryListBinding;
import com.example.mon.qrcodetrackingsystem.manager.DeliveryManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DeliveryListAdapter;
import com.example.mon.qrcodetrackingsystem.modules.scanner.SimpleScannerActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;

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
    private DeliveryListAdapter productAdapter;
    private List<Item> mItemList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(DeliveryListActivity.this, R.layout.activity_delivery_list);
        mBinding.loading.setVisibility(View.VISIBLE);
        mItemList = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        mProductList = SharedPreferenceManager.getInstance(this).getDeliveryProduct();
        mRecyclerView = mBinding.recyclerView;
        setUpProductAdapter();
        productAdapter.notifyDataSetChanged();

        //region Click
        RxUtils.clicks(mBinding.confirm)
            .subscribe(view -> {
                createDeliveryLog();
                updateItemOutForDelivery();
                createItemLog();
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
            ItemManager.getInstance().updateItemDeliveryStatus(mItemList.get(i).getId(), "Out For Delivery");
        }
    }

    private void createItemLog() {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();

        for (int i = 0; i < mItemList.size(); i++) {
            long unixTime = System.currentTimeMillis() / 1000L;
            ItemLogManager.getInstance().createNewItemLog(mItemList.get(i).getId(), currentUserId, unixTime, "Out For Delivery", "");
        }
    }

    private void createDeliveryLog() {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
        for (int i = 0; i < mItemList.size(); i++) {
            long unixTime = System.currentTimeMillis() / 1000L;
            DeliveryManager.getInstance().createNewDelivery(currentUserId, unixTime);
        }
    }

    private void setUpProductAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        productAdapter = new DeliveryListAdapter(mProductList);
        mRecyclerView.setAdapter(productAdapter);
        mBinding.loading.setVisibility(View.GONE);
    }
}
