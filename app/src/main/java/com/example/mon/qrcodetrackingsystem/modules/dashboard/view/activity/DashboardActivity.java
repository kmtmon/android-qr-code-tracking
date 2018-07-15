package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDashboardBinding;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DashboardProductAdapter;
import com.example.mon.qrcodetrackingsystem.modules.login.view.activity.LoginActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

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

        mRecyclerView = mBinding.recyclerView;

        //region Setup
        setUpProductAdapter();
        //endregion

        //region Click
        RxUtils.clicks(mBinding.add)
                .subscribe(view -> {
                    EditItemActivity.show(this);
                });

        RxUtils.clicks(mBinding.logout)
                .subscribe(view ->{
                    SharedPreferenceManager.getInstance(this).removeCurrentUserId();
                    LoginActivity.show(this);
                    finish();
                });
        //endregion

        //region Retrieve Data
        ProductManager.getInstance().retrieveProducts(productList -> {
            this.mProductList.clear();
            this.mProductList.addAll(productList);
            productAdapter.notifyDataSetChanged();
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
