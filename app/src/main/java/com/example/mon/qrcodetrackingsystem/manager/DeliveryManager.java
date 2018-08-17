package com.example.mon.qrcodetrackingsystem.manager;

import android.util.Log;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Delivery;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class DeliveryManager {

    private static DeliveryManager mInstance = null;

    public static DeliveryManager getInstance(){
        synchronized (DeliveryManager.class) {
            if (mInstance == null) {
                mInstance = new DeliveryManager();
            }
            return mInstance;
        }
    }

    public void createNewDelivery(String userId, long timestamp){
        Delivery newDelivery = new Delivery();
        newDelivery.setUserId(userId);
        newDelivery.setLat(0);
        newDelivery.setLng(0);
        newDelivery.setTimestamp(timestamp);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("delivery")
                .add(newDelivery)
                .addOnSuccessListener(documentReference ->
                        Log.d("", "Log DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("", "Error adding document", e));
    }


    public void updateItemCurrentLocation(String userId, long timestamp, double lat, double lng) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("delivery").whereEqualTo("userId", userId);

        query.get().addOnCompleteListener((Task<QuerySnapshot> task) -> {

            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        DocumentReference docRef = db.collection("delivery").document(document.getId());

                        docRef.update(
                                "lat", lat,
                                "lng", lng,
                                "timestamp", timestamp,
                                "userId", userId
                        ).addOnCompleteListener( t -> {
                            if (!t.isSuccessful()) {
                                //error
                            }
                        });
                    }
                }
            }
        });


    }

}
