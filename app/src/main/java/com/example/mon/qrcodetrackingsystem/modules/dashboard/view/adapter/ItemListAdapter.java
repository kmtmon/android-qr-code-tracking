package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mon.qrcodetrackingsystem.databinding.ItemItemBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;

import java.util.List;

/**
 * Created by mon on 12/7/18.
 */

public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Item> mItemList;

    public ItemListAdapter(List<Item> mItemList) {
        this.mItemList = mItemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        ItemItemBinding binding = ItemItemBinding.inflate(inflater, parent, false);
        viewHolder = new ItemViewHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder productViewHolder = (ItemViewHolder)holder;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ItemItemBinding mBinding;

        public ItemViewHolder(ItemItemBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }
}
