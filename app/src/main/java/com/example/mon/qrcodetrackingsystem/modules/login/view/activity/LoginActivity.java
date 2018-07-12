package com.example.mon.qrcodetrackingsystem.modules.login.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityLoginBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.DashboardActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

public class LoginActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        //region Click
        RxUtils.clicks(loginBinding.login)
                .subscribe(view -> {
                    navigateToDashboard();
                });
        //endregion
    }

    private void navigateToDashboard() {
        DashboardActivity.show(this);
    }
}
