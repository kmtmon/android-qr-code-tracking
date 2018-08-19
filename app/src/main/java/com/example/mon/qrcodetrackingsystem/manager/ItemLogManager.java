package com.example.mon.qrcodetrackingsystem.manager;

import android.util.Log;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemLogManager {

    private static ItemLogManager mInstance = null;

    public static ItemLogManager getInstance(){
        synchronized (ItemLogManager.class) {
            if (mInstance == null) {
                mInstance = new ItemLogManager();
            }
            return mInstance;
        }
    }

    //region Create
    public ItemLog createNewItemLog(String itemId, String userId, long timestamp, String status, String remark){
        ItemLog newItemLog = new ItemLog(itemId,userId,timestamp,status,remark);
        return  newItemLog;
    }
    //endregion

    //region Save
    public void saveItemLog(ItemLog log){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("log")
                .add(log)
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> {});
    }
    //endregion

    //region Retrieve
    public void retrieveItemLogs(String itemId,ItemLogsOnCompletionListener mListener){
        List<ItemLog> list = new ArrayList<ItemLog>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query docRef = db.collection("log")
                .whereEqualTo("itemId",itemId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    list.clear();
                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        ItemLog obj=document.toObject(ItemLog.class);
                        obj.setId(document.getId());
                        list.add(obj);
                    }
                    Collections.sort(list);
                }
            }

            mListener.ItemLogsOnCompletionListener(list);
        });
    }

    public void retrieveLatestItemLog(String itemId, LatestItemLogsOnCompletionListener mListener){

        List<ItemLog> list = new ArrayList<ItemLog>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query docRef = db.collection("log")
                .whereEqualTo("itemId",itemId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    list.clear();
                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        ItemLog obj=document.toObject(ItemLog.class);
                        obj.setId(document.getId());
                        list.add(obj);
                    }

                    Collections.sort(list);
                    if(list.size() > 0){
                        mListener.LatestItemLogsOnCompletionListener(list.get(0));
                    }
                }
            }

        });
    }
    //endregion

    public interface ItemLogsOnCompletionListener {
        void ItemLogsOnCompletionListener(List<ItemLog> itemLogList);
    }

    public interface LatestItemLogsOnCompletionListener {
        void LatestItemLogsOnCompletionListener(ItemLog itemLog);
    }


}
