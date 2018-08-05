package com.example.mon.qrcodetrackingsystem;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityLauncherBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.DashboardActivity;
import com.example.mon.qrcodetrackingsystem.modules.login.view.activity.LoginActivity;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import io.fabric.sdk.android.Fabric;

public class LauncherActivity extends BaseActivity {

    private String TAG = LauncherActivity.class.getSimpleName();

    ActivityLauncherBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        mBinding = DataBindingUtil.setContentView(LauncherActivity.this, R.layout.activity_launcher);

        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
        if(currentUserId != null &&
                !currentUserId.isEmpty()){
            DashboardActivity.show(this);
        }else{
            LoginActivity.show(this);
        }
        finish();
    }
}
