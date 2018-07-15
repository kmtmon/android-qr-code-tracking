package com.example.mon.qrcodetrackingsystem.modules.login.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityLoginBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity.DashboardActivity;
import com.example.mon.qrcodetrackingsystem.modules.login.objectmodel.User;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends BaseActivity {

    private String TAG = LoginActivity.class.getSimpleName();

    /** Entry */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        //region Click
        RxUtils.clicks(mBinding.login)
                .subscribe(view -> {
                    loginAction();
                });
        //endregion
    }

    private void loginAction() {
        String email = mBinding.email.getText().toString();
        String password = mBinding.password.getText().toString();

        if(email == null ||
                email.isEmpty() ||
                password == null ||
                password.isEmpty()){

            Toast.makeText(this, "Email and Password cannot be empty",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //region Firebase login integration

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query docRef = db.collection("user").whereEqualTo("email",email);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if(!task.getResult().isEmpty()){

                    User user=task.getResult().getDocuments().get(0).toObject(User.class);

                    if(user.getPassword()!= null &&
                            !user.getPassword().isEmpty() &&
                            user.getPassword().equalsIgnoreCase(password)){

                        /** Successful Login */

                        Toast.makeText(this, "Logged in successfully",
                                Toast.LENGTH_LONG).show();

                        String currentUserId = task.getResult().getDocuments().get(0).getId();
                        SharedPreferenceManager.getInstance(this).storeCurrentUserId(currentUserId);
                        DashboardActivity.show(this);
                        finish();

                    }else{
                        /** Invalid Password */
                        Toast.makeText(this, "Email or Password is wrong, try agin",
                                Toast.LENGTH_LONG).show();
                    }

                }else{
                    /** No User Exist */
                    Toast.makeText(this, "No User Found",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error getting documents: "+ task.getException(),
                        Toast.LENGTH_LONG).show();
            }
        });
        //endregion
    }
}
