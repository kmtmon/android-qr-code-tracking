package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityProductInfoBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.ItemListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoActivity extends Activity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, ProductInfoActivity.class);
        context.startActivity(intent);
    }

    ActivityProductInfoBinding mBinding;
    private RecyclerView mRecyclerView;

    private ItemListAdapter itemListAdapter;
    private List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ProductInfoActivity.this, R.layout.activity_product_info);

        mRecyclerView = mBinding.recyclerView;

        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());



        //region Setup
        setUpItemListAdapter();
        //endregion
    }

    private void setUpItemListAdapter(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        itemListAdapter = new ItemListAdapter(itemList);
        mRecyclerView.setAdapter(itemListAdapter);
    }
}
