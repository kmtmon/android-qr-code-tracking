package com.example.mon.qrcodetrackingsystem.manager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private static ItemManager mInstance = null;

    public static ItemManager getInstance(){
        synchronized (ItemManager.class) {
            if (mInstance == null) {
                mInstance = new ItemManager();
            }
            return mInstance;
        }
    }

    public Item createNewItem(String productId, String status, String remark, String description, double lat, double lng){
        Item newItem = new Item();
        newItem.setProductID(productId);
        newItem.setStatus(status);
        newItem.setRemark(remark);
        newItem.setDescription(description);
        newItem.setLat(lat);
        newItem.setLng(lng);
        return  newItem;
    }

    public void retrieveItem(String itemId, ItemOnCompletionListener mListener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("item").document(itemId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(task.getResult() != null){

                    Item obj=task.getResult().toObject(Item.class);
                    obj.setId(task.getResult().getId());

                    mListener.ItemOnCompletionListener(obj);
                }
            }
        });
    }

    public void retrieveItems(String productId, OnCompletionListener mListener) {

        List<Item> itemList = new ArrayList<Item>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query docRef = db.collection("item").whereEqualTo("productID",productId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    itemList.clear();
                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        Item item=document.toObject(Item.class);
                        item.setId(document.getId());
                        itemList.add(item);
                    }
                }
            }

            mListener.OnCompletionListener(itemList);
        });
    }

    public void retrieveExistingQty(String productId, IemExistingQtyOnCompletionListener mListener) {

        List<Item> itemList = new ArrayList<Item>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query docRef = db.collection("item").whereEqualTo("productID",productId);

        docRef.get().addOnCompleteListener((Task<QuerySnapshot> task) -> {

            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        Item item=document.toObject(Item.class);
                        item.setId(document.getId());
                        if(item.status != null && !item.status.isEmpty()){
                            if(item.status.equalsIgnoreCase(ItemStatusManager.IN_WAREHOUSE)){
                                itemList.add(item);
                            }
                        }
                    }
                }
            }

            mListener.IemExistingQtyOnCompletionListener(itemList.size());
        });
    }

    public void updateItemDeliveryStatus(String itemId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("item").document(itemId);

        docRef.update(
                "status", status,
                "remark", ""
        ).addOnCompleteListener( task -> {
            if (!task.isSuccessful()) {
               //error
            }
        });
    }

    public interface ItemOnCompletionListener {
        void ItemOnCompletionListener(Item item);
    }

    public interface OnCompletionListener {
        void OnCompletionListener(List<Item> itemList);
    }

    public interface IemExistingQtyOnCompletionListener {
        void IemExistingQtyOnCompletionListener(int qty);
    }
}
