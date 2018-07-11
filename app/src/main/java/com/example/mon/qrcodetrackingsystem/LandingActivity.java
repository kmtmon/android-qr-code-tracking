package com.example.mon.qrcodetrackingsystem;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityLandingBinding;

public class LandingActivity extends BaseActivity {

    private String TAG = LandingActivity.class.getSimpleName();

    ActivityLandingBinding activityLandingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLandingBinding = DataBindingUtil.setContentView(LandingActivity.this, R.layout.activity_landing);

        /*
        // Access a Cloud Firestore instance from your Activity

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
                */

    }
}
