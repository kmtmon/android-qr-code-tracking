package com.example.mon.qrcodetrackingsystem.ui;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.mon.qrcodetrackingsystem.databinding.DialogfragmentRecyclerviewBinding;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends DialogFragment {


    private Window mWindow;
    private Context mContext;

    private List<Object> objectList;
    private RecyclerViewAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter.RecyclerViewAdapterListener mListener;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            mWindow = getDialog().getWindow();
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);


            DialogfragmentRecyclerviewBinding mBinding = DialogfragmentRecyclerviewBinding.inflate(inflater, container, false);

            return mBinding.getRoot();
        } catch (Exception e) {

        }

        return null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        /** true - able to tap outside and cancel, false - otherwise */
        dialog.setCanceledOnTouchOutside(true);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return dialog;
    }

    @Override
    public void onResume() {
        try {
            int width = (int)(getResources().getDisplayMetrics().widthPixels*0.9);
            int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);

            if (getDialog() != null) {
                getDialog().getWindow().setLayout(width, height);
            }
        } catch (Exception e) {

        }
        super.onResume();
    }

    private void setUpItemListAdapter(){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mListAdapter = new RecyclerViewAdapter(objectList,mListener);
        mRecyclerView.setAdapter(mListAdapter);
    }
}
