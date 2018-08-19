package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityConfirmDeliveryBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemStatusManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

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


        RxUtils.clicks(mBinding.confirm)
                .subscribe(view -> {
                    updateItemOutForDelivery();
                    createItemLog();
                    SharedPreferenceManager.getInstance(this).removeDeliveryItem(mItem.getId());

                    if(SharedPreferenceManager.getInstance(this).getDeliveryItem().size() == 0){
                        /** Go to dashboard*/
                        Intent intent = new Intent(ConfirmDeliveryActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ConfirmDeliveryActivity.this.startActivity(intent);
                        finish();
                    }else{
                        /** Go back to delivery list*/
                        finish();
                    }
                    Toast.makeText(getBaseContext(), "Successfully confirmed delivery!", Toast.LENGTH_SHORT);
                });

        mBinding.loading.setVisibility(View.GONE);
    }

    private void updateItemOutForDelivery() {
        ItemManager.getInstance().updateItemDeliveryStatus(mItemId, ItemStatusManager.DELIVERED);
    }

    private void createItemLog() {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
        long unixTime = System.currentTimeMillis() / 1000L;
        ItemLog log = ItemLogManager.getInstance().createNewItemLog(mItemId, currentUserId, unixTime, ItemStatusManager.DELIVERED, "");
        ItemLogManager.getInstance().saveItemLog(log);
    }

    private void getItem() {
        mItemList = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).getId().equals(mItemId)) {
                mItem = mItemList.get(i);

                ProductManager.getInstance().retrieveProduct(mItem.getProductID(), product -> {
                    mProduct = product;

                    setupInfo();
                });
            }else{
                if (i == mItemList.size() - 1) {
                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setButtonsColorRes(R.color.colorPrimary)
                            .setMessage("Invalid Item")
                            .setTopTitleColor(R.color.pure_white)
                            .setPositiveButton("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    finish();

                                }
                            })
                            .show();
                }
            }
        }
    }

    private void setupInfo() {
        Picasso.get()
                .load(mProduct.imagePath)
                .into(mBinding.imageView);
        mBinding.title.setText(mProduct.getName());
        mBinding.itemId.setText("Item ID: "+mItem.getId());
//        mBinding.productId.setText(mProduct.getId());
    }
}
