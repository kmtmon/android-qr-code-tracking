package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityDashboardBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter.DashboardProductAdapter;
import com.example.mon.qrcodetrackingsystem.modules.login.view.activity.LoginActivity;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardActivity extends BaseActivity {

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }

    ActivityDashboardBinding mBinding;
    private RecyclerView mRecyclerView;

    private DashboardProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<Product>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(DashboardActivity.this, R.layout.activity_dashboard);

        mRecyclerView = mBinding.recyclerView;

        //region Setup
        setUpProductAdapter();
        //endregion

        //region Click
        RxUtils.clicks(mBinding.add)
                .subscribe(view -> {
//                    ProductInfoActivity.show(this);
                });

        RxUtils.clicks(mBinding.logout)
                .subscribe(view ->{
                    SharedPreferenceManager.getInstance(this).removeCurrentUserId();
                    LoginActivity.show(this);
                    finish();
                });
        //endregion

        //region Retrieve Data
        retrieveProducts();
        //endregion
    }

    //region Firebase
    private void retrieveProducts() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query docRef = db.collection("product");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    productList.clear();
                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        Product product=document.toObject(Product.class);
                        product.setId(document.getId());
                        productList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    //endregion

    private void setUpProductAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        productAdapter = new DashboardProductAdapter(productList, productId -> ProductInfoActivity.show(this,productId));
        mRecyclerView.setAdapter(productAdapter);
    }
}
