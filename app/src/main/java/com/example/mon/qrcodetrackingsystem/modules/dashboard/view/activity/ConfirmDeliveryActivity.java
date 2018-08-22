package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mon.qrcodetrackingsystem.R;
import com.example.mon.qrcodetrackingsystem.base.BaseActivity;
import com.example.mon.qrcodetrackingsystem.databinding.ActivityConfirmDeliveryBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemManager;
import com.example.mon.qrcodetrackingsystem.manager.ItemStatusManager;
import com.example.mon.qrcodetrackingsystem.manager.ProductManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.example.mon.qrcodetrackingsystem.utils.SharedPreferenceManager;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfirmDeliveryActivity extends BaseActivity {

    private String TAG = ConfirmDeliveryActivity.class.getSimpleName();
    private final static String ITEM_ID = "ITEM_ID";

    //region Entry

    /**
     * Entry
     */
    public static void show(Context context, String itemId) {
        Intent intent = new Intent(context, ConfirmDeliveryActivity.class);
        intent.putExtra(ITEM_ID, itemId);
        context.startActivity(intent);
    }
    //endregion

    ActivityConfirmDeliveryBinding mBinding;
    private String mItemId = null;

    private List<Item> mItemList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();
    private Item mItem = null;
    private Product mProduct = null;
    private boolean hasSigned = false;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mBinding = DataBindingUtil.setContentView(ConfirmDeliveryActivity.this, R.layout.activity_confirm_delivery);
        mBinding.loading.setVisibility(View.VISIBLE);

        mItemId = getIntent().getStringExtra(ITEM_ID);
        getItem();


        RxUtils.clicks(mBinding.confirm)
                .subscribe(view -> {
                    if(!hasSigned){
                        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setButtonsColorRes(R.color.colorPrimary)
                                .setMessage("Please get customer signature to confirm delivery")
                                .setTopTitleColor(R.color.pure_white)
                                .setPositiveButton("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();
                        return;
                    }

                    uploadSignature();

                });

        mBinding.loading.setVisibility(View.GONE);

        setUpSignaturePad();
    }

    private void setUpSignaturePad() {
        mBinding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                hasSigned = true;
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
            }
        });
    }

    private void updateItemOutForDelivery(String downloadUrl) {
        ItemManager.getInstance().updateItemDeliveryStatus(mItemId, ItemStatusManager.DELIVERED, downloadUrl);
    }

    private void uploadSignature() {

        mBinding.loading.setVisibility(View.VISIBLE);

        Bitmap signature = mBinding.signaturePad.getSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signature.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        StorageReference ref = storageReference.child("delivery/"+ mItemId);

        UploadTask uploadTask = ref.putBytes(byteArray);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e(TAG,"Download URL "+downloadUri.toString());

                    updateItemOutForDelivery(downloadUri.toString());
                    createItemLog(downloadUri.toString());

                    SharedPreferenceManager.getInstance(ConfirmDeliveryActivity.this).removeDeliveryItem(mItem.getId());

                    if(SharedPreferenceManager.getInstance(ConfirmDeliveryActivity.this).getDeliveryItem().size() == 0){
                        /** Go to dashboard*/
                        Intent intent = new Intent(ConfirmDeliveryActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ConfirmDeliveryActivity.this.startActivity(intent);
                        finish();
                    }else{
                        /** Go back to delivery list*/
                        finish();
                    }
                    mBinding.loading.setVisibility(View.GONE);
                } else {
                    mBinding.loading.setVisibility(View.GONE);
                }
            }
        });
    }

    private void createItemLog(String downloadUrl) {
        String currentUserId = SharedPreferenceManager.getInstance(this).getCurrentUserId();
        long unixTime = System.currentTimeMillis() / 1000L;
        ItemLog log = ItemLogManager.getInstance().createNewItemLog(mItemId, currentUserId, unixTime, ItemStatusManager.DELIVERED, downloadUrl);
        ItemLogManager.getInstance().saveItemLog(log);
    }

    private void getItem() {
        mItemList = SharedPreferenceManager.getInstance(this).getDeliveryItem();
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).getId().equals(mItemId)) {
                mItem = mItemList.get(i);

                ProductManager.getInstance().retrieveProduct(mItem.getProductID(), product -> {
                    mProduct = product;

                    setupInfo();
                });
            }else{
                if (i == mItemList.size() - 1) {
                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setButtonsColorRes(R.color.colorPrimary)
                            .setMessage("Invalid Item")
                            .setTopTitleColor(R.color.pure_white)
                            .setPositiveButton("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    finish();

                                }
                            })
                            .show();
                }
            }
        }
    }

    private void setupInfo() {

        if (mProduct.imagePath != null && !mProduct.imagePath.isEmpty()) {
            Picasso.get()
                    .load(mProduct.imagePath)
                    .into(mBinding.imageView);
            mBinding.title.setText(mProduct.getName());
            mBinding.itemId.setText("Item ID: " + mItem.getId());
        }
//        mBinding.productId.setText(mProduct.getId());
    }
}
