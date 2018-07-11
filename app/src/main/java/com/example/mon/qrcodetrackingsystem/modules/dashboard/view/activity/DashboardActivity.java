package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDashboardBinding;

public class DashboardActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }

    ActivityDashboardBinding dashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dashboardBinding = DataBindingUtil.setContentView(DashboardActivity.this, R.layout.activity_dashboard);
    }
}
