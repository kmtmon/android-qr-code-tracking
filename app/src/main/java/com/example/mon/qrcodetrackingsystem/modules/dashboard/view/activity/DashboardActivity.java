package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDashboardBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DashboardProductAdapter;
import com.example.mon.qrcodetrackingsystem.modules.login.view.activity.LoginActivity;
import com.example.mon.qrcodetrackingsystem.modules.scanner.SimpleScannerActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import static android.Manifest.permission.CAMERA;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class DashboardActivity extends BaseActivity {

    //region Entry
    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }
    //endregion

    private String TAG = DashboardActivity.class.getSimpleName();

    ActivityDashboardBinding mBinding;
    private RecyclerView mRecyclerView;

    private DashboardProductAdapter productAdapter;
    private List<Product> mProductList = new ArrayList<Product>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(DashboardActivity.this, R.layout.activity_dashboard);
        mBinding.loading.setVisibility(View.VISIBLE);
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserName(SharedPreferenceManager.getInstance(this).getCurrentUserId());

        mRecyclerView = mBinding.recyclerView;

        //region Setup
        setUpProductAdapter();
        //endregion

        //region Click
        RxUtils.clicks(mBinding.delivery)
                .subscribe(view -> {
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED){
                        SimpleScannerActivity.show(this, true, false);
                    }else{
                        ActivityCompat.requestPermissions(this,
                                new String[]{CAMERA},
                                1);
                    }
                });

        RxUtils.clicks(mBinding.add)
                .subscribe(view -> {
                    EditItemActivity.show(this);
                });

        RxUtils.clicks(mBinding.logout)
                .subscribe(view ->{
                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setButtonsColorRes(R.color.red)
                            .setMessage("Are you sure you want to log out?")
                            .setTopTitleColor(R.color.pure_white)
                            .setPositiveButton("Log Out", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferenceManager.getInstance(DashboardActivity.this).removeCurrentUserId();
                                    LoginActivity.show(DashboardActivity.this);
                                    finish();
                                }
                            })
                            .show();
                });

        RxUtils.clicks(mBinding.scanner)
                .subscribe(view ->{
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED){
                        SimpleScannerActivity.show(this, false, false);
                    }else{
                        ActivityCompat.requestPermissions(this,
                                new String[]{CAMERA},
                                1);
                    }

                });
        //endregion

        //region Retrieve Data
        ProductManager.getInstance().retrieveProducts(productList -> {
            this.mProductList.clear();
            this.mProductList.addAll(productList);
            productAdapter.notifyDataSetChanged();

            mBinding.loading.setVisibility(View.GONE);
        });
        //endregion

    }

    private void setUpProductAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        productAdapter = new DashboardProductAdapter(mProductList, productId -> ProductInfoActivity.show(this,productId));
        mRecyclerView.setAdapter(productAdapter);
    }
}
