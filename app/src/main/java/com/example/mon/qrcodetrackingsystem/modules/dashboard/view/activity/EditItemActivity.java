package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityEditItemBinding;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

public class EditItemActivity extends BaseActivity {

    //region Entry
    /** Entry */
    /**  */
    public static void show(Context context) {
        Intent intent = new Intent(context, EditItemActivity.class);
        context.startActivity(intent);
    }

    public static void show(Context context,String itemId) {
        Intent intent = new Intent(context, EditItemActivity.class);
        intent.putExtra(EditItemActivity.ITEM_ID, itemId);
        context.startActivity(intent);
    }
    //endregion

    public static String ITEM_ID = "ITEM_ID";

    ActivityEditItemBinding mBinding;

    private Item mItem;
    private List<Product> mProductList = new ArrayList<Product>();

    public ObservableBoolean isNewItem         = new ObservableBoolean(false);
    public ObservableBoolean isDisplayingInfo  = new ObservableBoolean(false);
    public ObservableBoolean isEditing         = new ObservableBoolean(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(EditItemActivity.this, R.layout.activity_edit_item);
        mBinding.setView(EditItemActivity.this);

        setUpForm();
        //region Click
        RxUtils.clicks(mBinding.log)
                .subscribe(view -> {
                    ItemLogActivity.show(this);
                });
        //endregion

        //region Retrieve Data
        ProductManager.getInstance().retrieveProducts(productList -> {
            this.mProductList.clear();
            this.mProductList.addAll(productList);
        });
        //endregion
    }

    private void setUpForm(){
        if(mItem == null){
            isNewItem.set(true);
            isDisplayingInfo.set(false);
            isEditing.set(false);
        }else{
            isNewItem.set(false);
            isDisplayingInfo.set(true);
            isEditing.set(false);
        }
    }


}
