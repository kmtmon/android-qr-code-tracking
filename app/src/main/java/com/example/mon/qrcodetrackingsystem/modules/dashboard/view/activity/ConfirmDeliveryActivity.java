package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityConfirmDeliveryBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ConfirmDeliveryActivity extends BaseActivity {

    private final static String ITEM_ID = "ITEM_ID";

    //region Entry
    /**
     * Entry
     */
    public static void show(Context context, String itemId) {
        Intent intent = new Intent(context, ConfirmDeliveryActivity.class);
        intent.putExtra(ITEM_ID, itemId);
        context.startActivity(intent);
    }
    //endregion

    ActivityConfirmDeliveryBinding mBinding;
    private String mItemId = null;

    private List<Item> mItemList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();
    private Item mItem = null;
    private Product mProduct = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ConfirmDeliveryActivity.this, R.layout.activity_confirm_delivery);
        mBinding.loading.setVisibility(View.VISIBLE);

        mItemId = getIntent().getStringExtra(ITEM_ID);
        getItem();
        getProduct();
        setupInfo();


        RxUtils.clicks(mBinding.confirm)
            .subscribe(view -> {
                updateItemOutForDelivery();
                createItemLog();
                SharedPreferenceManager.getInstance(this).removeDeliveryItem(mItem.getId());
                SharedPreferenceManager.getInstance(this).removeDeliveryProduct(mItem.getProductID());
                Toast.makeText(getBaseContext(), "Successfully confirmed delivery!", Toast.LENGTH_SHORT);
                finish();
            });

        mBinding.loading.setVisibility(View.GONE);
    }

    private void updateItemOutForDelivery() {
        ItemManager.getInstance().updateItemDeliveryStatus(mItemId, "Delivered");
    }

    private void createItemLog() {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
        long unixTime = System.currentTimeMillis() / 1000L;
        ItemLogManager.getInstance().createNewItemLog(mItemId, currentUserId, unixTime, "Delivered", "");
    }

    private void getItem() {
        mItemList = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).getId().equals(mItemId)) {
                mItem = mItemList.get(i);
            }
        }
    }

    private void getProduct() {
        mProductList = SharedPreferenceManager.getInstance(this).getDeliveryProduct();
        for(int i = 0; i < mProductList.size(); i++) {
            if (mProductList.get(i).getId().equals(mItem.getProductID())) {
                mProduct = mProductList.get(i);
            }
        }
    }

    private void setupInfo() {
        Picasso.get()
                .load(mProduct.imagePath)
                .into(mBinding.imageView);
        mBinding.title.setText(mProduct.getName());
        mBinding.itemId.setText(mItem.getId());
        mBinding.productId.setText(mProduct.getId());
    }
}
