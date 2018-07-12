package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDashboardBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DashboardProductAdapter;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }

    ActivityDashboardBinding dashboardBinding;
    private RecyclerView mRecyclerView;

    private DashboardProductAdapter productAdapter;
    private List<String> productList = new ArrayList<String>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardBinding = DataBindingUtil.setContentView(DashboardActivity.this, R.layout.activity_dashboard);

        mRecyclerView = dashboardBinding.recyclerView;
        productList = Arrays.asList("sup1", "sup2", "sup3");

        //region Setup
        setUpProductAdapter();
        //endregion

        //region Click
        RxUtils.clicks(dashboardBinding.add)
                .subscribe(view -> {
                    EditItemActivity.show(this);
                });
        //endregion
    }

    private void setUpProductAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        productAdapter = new DashboardProductAdapter(productList);
        mRecyclerView.setAdapter(productAdapter);
    }
}
