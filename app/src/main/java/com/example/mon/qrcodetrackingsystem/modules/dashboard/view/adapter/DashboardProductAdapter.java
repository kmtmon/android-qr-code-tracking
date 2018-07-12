package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mon.qrcodetrackingsystem.databinding.ItemDashboardProductBinding;

import java.util.List;

/**
 * Created by mon on 12/7/18.
 */

public class DashboardProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mProductList;

    public DashboardProductAdapter(List<String> mProductList) {
        this.mProductList = mProductList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        ItemDashboardProductBinding agentFinderBinding = ItemDashboardProductBinding.inflate(inflater, parent, false);
        viewHolder = new DashBoardProductViewHolder(agentFinderBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DashBoardProductViewHolder productViewHolder = (DashBoardProductViewHolder)holder;
        productViewHolder.mBinding.title.setText(mProductList.get(position));
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    static class DashBoardProductViewHolder extends RecyclerView.ViewHolder {
        ItemDashboardProductBinding mBinding;

        DashBoardProductViewHolder(ItemDashboardProductBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
