package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityProductInfoBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.ItemListAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoActivity extends Activity implements ItemListAdapter.ItemListAdapterListner {

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
    private List<Item> mItemList = new ArrayList<>();

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
        mRecyclerView.setNestedScrollingEnabled(false);

        //region Setup
        setUpItemListAdapter();
        //endregion

        retrieveProductInfo();
        retrieveItemWithProductId();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.loading.setVisibility(View.VISIBLE);

        retrieveItemWithProductId();
        retrieveExistingQtyProductId();
    }

    //region Firebase
    private void retrieveProductInfo(){
        ProductManager.getInstance().retrieveProduct(mProductId, product -> {
            mProduct=product;
            setUpProductInfo();
        });

        ItemManager.getInstance().retrieveExistingQty(mProductId, existingQty -> {
            setUpExistingQty(existingQty);
        });
    }

    private void retrieveItemWithProductId(){
        ItemManager.getInstance().retrieveItems(mProductId, itemList -> {

            mItemList.clear();
            mItemList.addAll(itemList);
            itemListAdapter.notifyDataSetChanged();
        });
    }

    private void retrieveExistingQtyProductId(){
        ItemManager.getInstance().retrieveExistingQty(mProductId, existingQty -> {
            setUpExistingQty(existingQty);
        });
    }
    //endregion

    private void setUpItemListAdapter(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        itemListAdapter = new ItemListAdapter(mItemList,this);
        mRecyclerView.setAdapter(itemListAdapter);
    }

    private void setUpProductInfo(){

        if (mProduct != null) {

            if(mProduct.getImagePath() != null && !mProduct.getImagePath().isEmpty()){

                Picasso.get()
                        .load(mProduct.imagePath)
                        .into(mBinding.imageView);
                mBinding.imageView.setVisibility(View.VISIBLE);
            }else{
                mBinding.imageView.setVisibility(View.GONE);
            }

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

    private void setUpExistingQty(int qty){
        mBinding.loading.setVisibility(View.GONE);
        mBinding.existingQtyLayout.setVisibility(View.VISIBLE);
        mBinding.quantity.setText(""+qty);

    }

    @Override
    public void clickOnItem(Item item) {
        if(item.getId() != null){
            EditItemActivity.show(this,item.getId());
        }
    }
}
