package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityEditItemBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemStatusManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.ui.DialogRecyclerViewFragment;
import com.example.mon.qrcodetrackingsystem.ui.RecyclerViewAdapter;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

public class EditItemActivity extends BaseActivity implements RecyclerViewAdapter.RecyclerViewAdapterListener {

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
    private String TAG = EditItemActivity.class.getSimpleName();

    ActivityEditItemBinding mBinding;

    private Item mItem;
    private List<Product> mProductList = new ArrayList<Product>();

    private Product mChosenProduct;

    public ObservableBoolean isNewItem         = new ObservableBoolean(false);
    public ObservableBoolean isDisplayingInfo  = new ObservableBoolean(false);
    public ObservableBoolean isEditing         = new ObservableBoolean(false);
    public ObservableBoolean isStatusInWareHouse         = new ObservableBoolean(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(EditItemActivity.this, R.layout.activity_edit_item);
        mBinding.setView(EditItemActivity.this);

        setUpForm();

        //region Click
        RxUtils.clicks(mBinding.product)
                .subscribe(view -> {
                    popUpProductDialog();
                });

        RxUtils.clicks(mBinding.status)
                .subscribe(view -> {
                    popUpStatusDialog();
                });

        RxUtils.clicks(mBinding.log)
                .subscribe(view -> {
                    ItemLogActivity.show(this);
                });
        //endregion

        //region Retrieve Product Data
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

    DialogRecyclerViewFragment dialogFragment;

    public void popUpProductDialog() {
        Fragment oldFrag = getFragmentManager().findFragmentByTag("dialogfragment");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (oldFrag != null) {
            fragmentTransaction.remove(oldFrag);
        }
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(mProductList);

        dialogFragment = new DialogRecyclerViewFragment(this,objectList, this);

        dialogFragment.show(fragmentTransaction, "dialogfragment");
    }

    public void popUpStatusDialog() {
        Fragment oldFrag = getFragmentManager().findFragmentByTag("dialogfragment");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (oldFrag != null) {
            fragmentTransaction.remove(oldFrag);
        }
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(ItemStatusManager.getStatusArray());

        dialogFragment = new DialogRecyclerViewFragment(this,objectList, this);

        dialogFragment.show(fragmentTransaction, "dialogfragment");
    }

    @Override
    public void clickOnItem(Object object) {
        if(object instanceof Product){

            Product product = (Product) object;
            mChosenProduct = product;
            mBinding.product.setText(product.getName());
        }

        if(object instanceof String){
            String status = (String) object;
            mBinding.status.setText(status);

            isStatusInWareHouse.set(status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE));
        }

        dialogFragment.dismiss();
    }
}
