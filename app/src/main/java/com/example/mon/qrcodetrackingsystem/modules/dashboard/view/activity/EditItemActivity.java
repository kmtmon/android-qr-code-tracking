package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.mon.qrcodetrackingsystem.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditItemActivity extends BaseActivity implements RecyclerViewAdapter.RecyclerViewAdapterListener {

    //region Entry
    /** Entry */
    /**  */
    public static void show(Context context) {
        Intent intent = new Intent(context, EditItemActivity.class);
        context.startActivity(intent);
    }

    public static void show(Context context, String itemId) {
        Intent intent = new Intent(context, EditItemActivity.class);
        intent.putExtra(EditItemActivity.ITEM_ID, itemId);
        context.startActivity(intent);
    }
    //endregion

    public final static String ITEM_ID = "ITEM_ID";
    private String TAG = EditItemActivity.class.getSimpleName();

    ActivityEditItemBinding mBinding;
    DialogRecyclerViewFragment dialogFragment;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mItemId = null;
    private Item mItem;
    private List<Product> mProductList = new ArrayList<Product>();

    private Product mChosenProduct;

    public ObservableBoolean isNewItem = new ObservableBoolean(false);
    public ObservableBoolean isDisplayingInfo = new ObservableBoolean(false);
    public ObservableBoolean isEditing = new ObservableBoolean(false);
    public ObservableBoolean isStatusInWareHouse = new ObservableBoolean(false);

    @Override
    protected void onResume() {
        super.onResume();
        if (mItem != null) {
            mBinding.loading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(EditItemActivity.this, R.layout.activity_edit_item);
        mBinding.setView(EditItemActivity.this);

        mItemId = getIntent().getStringExtra(EditItemActivity.ITEM_ID);

        if (mItemId != null && !mItemId.isEmpty()) {
            mBinding.loading.setVisibility(View.VISIBLE);
            ItemManager.getInstance().retrieveItem(mItemId, item -> {
                mItem = item;
                setUpItemInfo();
            });
        }

        setUpForm();

        //region Click
        RxUtils.clicks(mBinding.qr)
                .subscribe(view -> {
                    mBinding.loading.setVisibility(View.VISIBLE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            generateQR();
                        }
                    }, 200);

                });

        RxUtils.clicks(mBinding.product)
                .subscribe(view -> {
                    if (isEditing.get() || isNewItem.get())
                        popUpProductDialog();
                });

        RxUtils.clicks(mBinding.status)
                .subscribe(view -> {
                    if (isEditing.get() || isNewItem.get())
                        popUpStatusDialog();
                });

        RxUtils.clicks(mBinding.log)
                .subscribe(view -> ItemLogActivity.show(this, mItemId));

        RxUtils.clicks(mBinding.add)
                .subscribe(view -> {

                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setButtonsColorRes(R.color.colorPrimary)
                            .setMessage("Are you sure you want to add the item?")
                            .setTopTitleColor(R.color.pure_white)
                            .setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addItem();
                                }
                            })
                            .show();
                });

        RxUtils.clicks(mBinding.save)
                .subscribe(view -> {

                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setButtonsColorRes(R.color.colorPrimary)
                            .setMessage("Are you sure you want to save the item?")
                            .setTopTitleColor(R.color.pure_white)
                            .setPositiveButton("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveItem();
                                }
                            })
                            .show();

                });

        RxUtils.clicks(mBinding.cancel)
                .subscribe(view -> {
                    isNewItem.set(false);
                    isDisplayingInfo.set(true);
                    isEditing.set(false);
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

    private void setUpForm() {
        if (mItemId == null) {
            isNewItem.set(true);
            isDisplayingInfo.set(false);
            isEditing.set(false);
        } else {
            isNewItem.set(false);
            isDisplayingInfo.set(true);
            isEditing.set(false);
        }
    }

    private void setUpItemInfo() {
        if (mItem == null) {
            return;
        }

        if (mItem.getProductID() != null) {
            ProductManager.getInstance().retrieveProduct(mItem.getProductID(), product -> {
                if (product.getName() != null) {
                    mBinding.product.setText(product.getName());
                }
            });

            if (mItem.status != null) {
                mBinding.status.setText(mItem.status);

                if (mItem.status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)) {
                    isStatusInWareHouse.set(true);
                    if (mItem.remark != null && !mItem.remark.isEmpty()) {
                        String[] parts = mItem.remark.split("-");

                        if (parts.length == 4) {
                            String formatted_FloorLevel = parts[0].replace("FL", "");
                            String formatted_RackNumber = parts[1].replace("RN", "");
                            String formatted_RackLevel = parts[2].replace("RL", "");
                            String formatted_RackColumn = parts[3].replace("RC", "");

                            mBinding.floorlevel.setText(formatted_FloorLevel);
                            mBinding.racknumber.setText(formatted_RackNumber);
                            mBinding.racklevel.setText(formatted_RackLevel);
                            mBinding.rackcolumn.setText(formatted_RackColumn);
                        }
                    }
                }
            }

            if (mItem.getDescription() != null && !mItem.getDescription().isEmpty()) {
                mBinding.description.setText(mItem.getDescription());
            }

            ItemLogManager.getInstance().retrieveLatestItemLog(mItem.getId(), itemLog -> {
                if (itemLog.timestamp != 0) {
                    mBinding.updatedAt.setText("Last updated at " + Utils.getTimeStringFromTimeStamp(itemLog.timestamp));
                }

            });

            mBinding.loading.setVisibility(View.GONE);
        }
    }

    //region QR
    private void generateQR() {

        if (mItem == null) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("id", mItem.id);

        Gson gson = new Gson();
        String json = gson.toJson(map);

        ItemQRActivity.show(this, json);

    }
    //endregion

    //region Pop Up

    public void popUpProductDialog() {
        Fragment oldFrag = getFragmentManager().findFragmentByTag("dialogfragment");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (oldFrag != null) {
            fragmentTransaction.remove(oldFrag);
        }
        List<Object> objectList = new ArrayList<>();
        objectList.addAll(mProductList);

        dialogFragment = new DialogRecyclerViewFragment(this, objectList, this);

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

        dialogFragment = new DialogRecyclerViewFragment(this, objectList, this);

        dialogFragment.show(fragmentTransaction, "dialogfragment");
    }

    @Override
    public void clickOnItem(Object object) {
        if (object instanceof Product) {

            Product product = (Product) object;
            mChosenProduct = product;
            mBinding.product.setText(product.getName());
        }

        if (object instanceof String) {
            String status = (String) object;
            mBinding.status.setText(status);

            isStatusInWareHouse.set(status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE));
        }

        dialogFragment.dismiss();
    }
    //endregion

    //region Add Item
    private void addItem() {

        if (mChosenProduct == null ||
                mBinding.status.getText().toString() == null ||
                mBinding.status.getText().toString().isEmpty()) {
            Toast.makeText(this, "Choose the product and status first", Toast.LENGTH_LONG).show();
            return;
        }

        String status = mBinding.status.getText().toString();
        String mRemark = "";
        if (status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)) {
            mRemark = "FL" + mBinding.floorlevel.getText().toString() + "-" +
                    "RN" + mBinding.racknumber.getText().toString() + "-" +
                    "RL" + mBinding.racklevel.getText().toString() + "-" +
                    "RC" + mBinding.rackcolumn.getText().toString();
        }

        String mDescription = mBinding.description.getText().toString();

        Item newItem = ItemManager.getInstance().createNewItem(mChosenProduct.getId(), status, mRemark, mDescription, 0, 0);
//        Item newItem = ItemManager.getInstance().createNewItem(mChosenProduct.getId(), status, mRemark, mDescription, mLat, mLng);

        db.collection("item")
                .add(newItem)
                .addOnSuccessListener(documentReference -> {

                    Log.d(TAG, "Item DocumentSnapshot written with ID: " + documentReference.getId());

                    String newItemId = documentReference.getId();
                    String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
                    long unixTime = System.currentTimeMillis() / 1000L;

                    String remark = "";
                    if (status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)) {
                        remark = "FL" + mBinding.floorlevel.getText().toString() + "-" +
                                "RN" + mBinding.racknumber.getText().toString() + "-" +
                                "RL" + mBinding.racklevel.getText().toString() + "-" +
                                "RC" + mBinding.rackcolumn.getText().toString();
                    }

                    ItemLog newItemLog = ItemLogManager.getInstance().createNewItemLog(newItemId, currentUserId, unixTime, mBinding.status.getText().toString(), remark);
                    addItemLog(newItemLog);

                    isNewItem.set(false);
                    isDisplayingInfo.set(true);
                    isEditing.set(false);

                    /** Set newly added item to mItem */
                    newItem.setId(newItemId);
                    mItem = newItem;
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


    }

    private void addItemLog(ItemLog log) {
        db.collection("log")
                .add(log)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Log DocumentSnapshot written with ID: " + documentReference.getId());

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
    //endregion

    //region Save Item
    private void saveItem() {

        if (okayToSave()) {

            String status = mBinding.status.getText().toString();
            String mRemark = "";
            if (status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)) {
                mRemark = "FL" + mBinding.floorlevel.getText().toString() + "-" +
                        "RN" + mBinding.racknumber.getText().toString() + "-" +
                        "RL" + mBinding.racklevel.getText().toString() + "-" +
                        "RC" + mBinding.rackcolumn.getText().toString();
            }

            String mDescription = mBinding.description.getText().toString();

            DocumentReference docRef = db.collection("item").document(mItemId);

            docRef
                    .update(
                            "status", status,
                            "remark", mRemark, "description", mDescription
                    )
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));


            String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
            long unixTime = System.currentTimeMillis() / 1000L;

            ItemLog newItemLog = ItemLogManager.getInstance().createNewItemLog(mItemId, currentUserId, unixTime, status, mRemark);
            addItemLog(newItemLog);

            isNewItem.set(false);
            isDisplayingInfo.set(true);
            isEditing.set(false);

            mBinding.updatedAt.setText("Last updated at " + Utils.getTimeStringFromTimeStamp(newItemLog.timestamp));
        } else {
            Toast.makeText(this, "Unable to save please check your input",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean okayToSave() {
        if (mItem == null) {
            return false;
        }

        String status = mBinding.status.getText().toString();

        if (status == null || status.isEmpty()) {
            return false;
        }

        if (mItem.status != null) {
            if (mItem.status.equalsIgnoreCase(status)) {

                String mRemark = "";
                if (status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)) {
                    mRemark = "FL" + mBinding.floorlevel.getText().toString() + "-" +
                            "RN" + mBinding.racknumber.getText().toString() + "-" +
                            "RL" + mBinding.racklevel.getText().toString() + "-" +
                            "RC" + mBinding.rackcolumn.getText().toString();
                }

                if (mItem.remark != null && mItem.remark.equalsIgnoreCase(mRemark)) {

                    String description = mBinding.description.getText().toString();
                    if (mItem.description != null && mItem.description.equalsIgnoreCase(description)) {
                        return false;
                    }

                }
            }
        }
        return true;
    }
    //endregion
}
