package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityProductInfoBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.ItemListAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoActivity extends Activity {

    //region Entry
    /** Entry */
    public static void show(Context context, String productId) {
        Intent intent = new Intent(context, ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.PRODUCT_ID,productId);
        context.startActivity(intent);
    }
    //endregion

    public static String PRODUCT_ID = "PRODUCT_ID";

    private String TAG = ProductInfoActivity.class.getSimpleName();

    ActivityProductInfoBinding mBinding;
    private RecyclerView mRecyclerView;

    private String mProductId = null;
    private Product mProduct;
    private ItemListAdapter itemListAdapter;
    private List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ProductInfoActivity.this, R.layout.activity_product_info);

        mProductId = getIntent().getStringExtra(ProductInfoActivity.PRODUCT_ID);

        if(mProductId == null || mProductId.isEmpty()){
            finish();
            return;
        }

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

        retrieveProductInfo();
    }

    //region Firebase
    private void retrieveProductInfo(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("product").document(mProductId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(task.getResult() != null){

                    mProduct=task.getResult().toObject(Product.class);

                    setUpProductInfo();
                }
            }
        });
    }
    //endregion

    private void setUpItemListAdapter(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        itemListAdapter = new ItemListAdapter(itemList);
        mRecyclerView.setAdapter(itemListAdapter);
    }

    private void setUpProductInfo(){
        if (mProduct != null) {

            if(mProduct.getName() == null ||
            mProduct.getName().isEmpty() ||
                    mProduct.getDesc() == null ||
                    mProduct.getDesc().isEmpty()){
                return;
            }
            mBinding.product.setText(mProduct.getName());
            mBinding.description.setText(mProduct.getDesc());
        }
    }
}
