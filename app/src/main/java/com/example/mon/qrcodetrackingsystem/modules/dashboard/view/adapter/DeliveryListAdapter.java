package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mon.qrcodetrackingsystem.databinding.ItemDeliveryListBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mon on 12/7/18.
 */

public class DeliveryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = DeliveryListAdapter.class.getSimpleName();

    private List<Product> mProductList;

    public DeliveryListAdapter(List<Product> mProductList) {
        this.mProductList = mProductList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        ItemDeliveryListBinding agentFinderBinding = ItemDeliveryListBinding.inflate(inflater, parent, false);
        viewHolder = new DeliveryListViewHolder(agentFinderBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Product product = mProductList.get(position);
        DeliveryListViewHolder productViewHolder = (DeliveryListViewHolder)holder;
        productViewHolder.mBinding.title.setText(product.getName());

        if(product.getImagePath() != null && !product.getImagePath().isEmpty()){

            Picasso.get()
                    .load(product.imagePath)
                    .into(productViewHolder.mBinding.imageView);
        }else{

        }

    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    static class DeliveryListViewHolder extends RecyclerView.ViewHolder {
        ItemDeliveryListBinding mBinding;

        DeliveryListViewHolder(ItemDeliveryListBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }

    public interface DeliveryListListener {
        void launchProductInfo(String productId);

    }
}
