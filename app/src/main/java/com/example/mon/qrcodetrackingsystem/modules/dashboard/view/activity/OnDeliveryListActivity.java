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
import com.example.mon.qrcodetrackingsystem.databinding.ActivityOnDeliveryListBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DeliveryListAdapter;
import com.example.mon.qrcodetrackingsystem.modules.scanner.SimpleScannerActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;

public class OnDeliveryListActivity extends BaseActivity {

    //region Entry

    /**
     * Entry
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, OnDeliveryListActivity.class);
        context.startActivity(intent);
    }
    //endregion

    ActivityOnDeliveryListBinding mBinding;

    private String TAG = OnDeliveryListActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private DeliveryListAdapter productAdapter;
    private List<Item> mItemList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(OnDeliveryListActivity.this, R.layout.activity_on_delivery_list);
        mBinding.loading.setVisibility(View.VISIBLE);
        mItemList = SharedPreferenceManager.getInstance(this).getDeliveryItem();
//        mProductList = SharedPreferenceManager.getInstance(this).getDeliveryProduct();

        mRecyclerView = mBinding.recyclerView;
        setUpProductAdapter();
        productAdapter.notifyDataSetChanged();

        RxUtils.clicks(mBinding.scanner)
            .subscribe(view ->{
                if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED){
                    SimpleScannerActivity.show(this, false, true);
                }else{
                    ActivityCompat.requestPermissions(this,
                            new String[]{CAMERA},
                            1);
                }

            });

    }

    private void setUpProductAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        productAdapter = new DeliveryListAdapter(mItemList);
        mRecyclerView.setAdapter(productAdapter);
        mBinding.loading.setVisibility(View.GONE);
    }

}
