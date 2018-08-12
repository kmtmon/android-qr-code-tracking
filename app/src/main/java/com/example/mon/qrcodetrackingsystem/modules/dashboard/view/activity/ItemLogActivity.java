package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityItemLogBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DashboardProductAdapter;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.ItemLogAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLogActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context, String itemId) {
        Intent intent = new Intent(context, ItemLogActivity.class);
        intent.putExtra(ItemLogActivity.ITEM_ID, itemId);
        context.startActivity(intent);
    }

    public static String ITEM_ID = "ITEM_ID";
    private String TAG = ItemLogActivity.class.getSimpleName();

    ActivityItemLogBinding mBinding;
    private RecyclerView mRecyclerView;

    private String mItemId;
    private ItemLogAdapter logAdapter;
    private List<ItemLog> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ItemLogActivity.this, R.layout.activity_item_log);

        mRecyclerView = mBinding.recyclerView;

        mItemId = getIntent().getStringExtra(ITEM_ID);

        //region Setup
        setUpLogAdapter();
        //endregion

        if(mItemId != null){
            mBinding.loading.setVisibility(View.VISIBLE);
            ItemLogManager.getInstance().retrieveItemLogs(mItemId, itemLogList -> {
                logList.clear();
                logList.addAll(itemLogList);
                logAdapter.notifyDataSetChanged();
                mBinding.loading.setVisibility(View.GONE);
            });
        }else{
            Log.e(TAG,"mItemId is null");
        }

    }

    private void setUpLogAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        logAdapter = new ItemLogAdapter(logList);
        mRecyclerView.setAdapter(logAdapter);
    }
}
