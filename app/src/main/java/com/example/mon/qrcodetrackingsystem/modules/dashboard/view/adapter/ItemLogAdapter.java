package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mon.qrcodetrackingsystem.databinding.ItemLogBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;

import java.util.List;

/**
 * Created by mon on 12/7/18.
 */

public class ItemLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ItemLog> mItemLogList;

    public ItemLogAdapter(List<ItemLog> mItemLogList) {
        this.mItemLogList = mItemLogList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        ItemLogBinding binding = ItemLogBinding.inflate(inflater, parent, false);
        viewHolder = new ItemLogViewHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemLogViewHolder productViewHolder = (ItemLogViewHolder)holder;
    }

    @Override
    public int getItemCount() {
        return mItemLogList.size();
    }

    static class ItemLogViewHolder extends RecyclerView.ViewHolder {
        ItemLogBinding mBinding;

        public ItemLogViewHolder(ItemLogBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }
}