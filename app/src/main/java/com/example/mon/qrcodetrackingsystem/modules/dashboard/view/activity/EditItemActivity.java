package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityEditItemBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemStatusManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.ui.DialogRecyclerViewFragment;
import com.example.mon.qrcodetrackingsystem.ui.RecyclerViewAdapter;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    DialogRecyclerViewFragment dialogFragment;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mItemId = null;
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

        mItemId = getIntent().getStringExtra(EditItemActivity.ITEM_ID);

        ItemManager.getInstance().retrieveItem(mItemId, item -> {
            mItem = item;
            setUpItemInfo();
        });

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

        RxUtils.clicks(mBinding.add)
                .subscribe(view -> {
                    addItem();
                });

        RxUtils.clicks(mBinding.save)
                .subscribe(view -> {
                    saveItem();
                });

        RxUtils.clicks(mBinding.edit)
                .subscribe(view -> {
                    isNewItem.set(false);
                    isDisplayingInfo.set(false);
                    isEditing.set(true);
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
        if(mItemId == null){
            isNewItem.set(true);
            isDisplayingInfo.set(false);
            isEditing.set(false);
        }else{
            isNewItem.set(false);
            isDisplayingInfo.set(true);
            isEditing.set(false);
        }
    }

    private void setUpItemInfo() {
        if(mItem == null){
            return;
        }

        if(mItem.getProductID()!= null){
            ProductManager.getInstance().retrieveProduct(mItem.getProductID(), product -> {
                if(product.getName() != null){
                    mBinding.product.setText(product.getName());
                }
            });

            ItemLogManager.getInstance().retrieveLatestItemLog(mItem.getId(), itemLog -> {
                if(itemLog.status != null){
                    mBinding.status.setText(itemLog.status);
                }


                if(itemLog.remark != null){

                }
            });
        }
    }

    //region Pop Up

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
    //endregion

    //region Add Item
    private void addItem(){

        if(mChosenProduct == null ||
                mBinding.status.getText().toString() == null ||
                mBinding.status.getText().toString().isEmpty()){
            Toast.makeText(this, "Choose the product and status first", Toast.LENGTH_LONG).show();
            return;
        }

        Item newItem = ItemManager.getInstance().createNewItem(mChosenProduct.getId());



        db.collection("item")
                .add(newItem)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Item DocumentSnapshot written with ID: " + documentReference.getId());
                    String newItemId = documentReference.getId();
                    String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
                    long unixTime = System.currentTimeMillis() / 1000L;

                    ItemLog newItemLog = ItemLogManager.getInstance().createNewItemLog(newItemId, currentUserId,unixTime,mBinding.status.getText().toString(),"");
                    addItemLog(newItemLog);

                    isNewItem.set(false);
                    isDisplayingInfo.set(true);
                    isEditing.set(false);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


    }

    private void addItemLog(ItemLog log){
        db.collection("log")
                .add(log)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Log DocumentSnapshot written with ID: " + documentReference.getId());

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
    //endregion

    //region Save Item
    private void saveItem(){
        isNewItem.set(false);
        isDisplayingInfo.set(true);
        isEditing.set(false);
    }
    //endregion
}
