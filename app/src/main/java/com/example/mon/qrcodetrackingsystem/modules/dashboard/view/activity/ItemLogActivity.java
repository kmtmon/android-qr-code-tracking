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
import com.example.mon.qrcodetrackingsystem.databinding.ActivityItemLogBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DashboardProductAdapter;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.ItemLogAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLogActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, ItemLogActivity.class);
        context.startActivity(intent);
    }

    ActivityItemLogBinding mBinding;
    private RecyclerView mRecyclerView;

    private ItemLogAdapter logAdapter;
    private List<ItemLog> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ItemLogActivity.this, R.layout.activity_item_log);

        mRecyclerView = mBinding.recyclerView;

        //region Setup
        setUpLogAdapter();
        //endregion

        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
        logList.add(new ItemLog());
    }

    private void setUpLogAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        logAdapter = new ItemLogAdapter(logList);
        mRecyclerView.setAdapter(logAdapter);
    }
}
