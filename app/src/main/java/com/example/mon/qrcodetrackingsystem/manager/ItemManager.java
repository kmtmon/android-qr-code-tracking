package com.example.mon.qrcodetrackingsystem.manager;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    public Item createNewItem(String productId){
        Item newItem = new Item();
        newItem.setProductID(productId);
        return  newItem;
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

    public interface OnCompletionListener {
        void OnCompletionListener(List<Item> itemList);
    }
}
