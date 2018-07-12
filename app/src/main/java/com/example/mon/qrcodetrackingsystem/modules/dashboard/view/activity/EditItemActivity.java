package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityEditItemBinding;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

public class EditItemActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, EditItemActivity.class);
        context.startActivity(intent);
    }

    ActivityEditItemBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(EditItemActivity.this, R.layout.activity_edit_item);

        //region Click
        RxUtils.clicks(mBinding.log)
                .subscribe(view -> {
                    ItemLogActivity.show(this);
                });
        //endregion
    }
}
