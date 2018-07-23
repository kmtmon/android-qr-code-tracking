package com.example.mon.qrcodetrackingsystem.manager;

import android.util.Log;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductManager {

    private String TAG = ProductManager.class.getSimpleName();
    private static ProductManager mInstance = null;

    public static ProductManager getInstance(){
        synchronized (ProductManager.class) {
            if (mInstance == null) {
                mInstance = new ProductManager();
            }
            return mInstance;
        }
    }

    public void retrieveProduct(String productId, ProductOnCompletionListener mListner) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("product").document(productId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(task.getResult() != null){

                    Product product=task.getResult().toObject(Product.class);

                    mListner.ProductOnCompletionListener(product);
                }
            }
        });
    }

    public void retrieveProducts(OnCompletionListener mListener) {

        List<Product> productList = new ArrayList<Product>();
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
                }
            }

            mListener.OnCompletionListener(productList);
        });
    }

    public interface OnCompletionListener {
        void OnCompletionListener(List<Product> productList);
    }

    public interface ProductOnCompletionListener {
        void ProductOnCompletionListener(Product product);
    }
}
