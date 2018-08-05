package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mon.qrcodetrackingsystem.databinding.ItemDashboardProductBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mon on 12/7/18.
 */

public class DashboardProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = DashboardProductAdapter.class.getSimpleName();

    private List<Product> mProductList;
    private DashboardProductListener mListener;

    public DashboardProductAdapter(List<Product> mProductList, DashboardProductListener listener) {
        this.mProductList = mProductList;
        this.mListener = listener;
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
        Product product = mProductList.get(position);

        DashBoardProductViewHolder productViewHolder = (DashBoardProductViewHolder)holder;
        productViewHolder.mBinding.title.setText(product.getName());

        if(product.getImagePath() != null && !product.getImagePath().isEmpty()){

            Picasso.get()
                    .load(product.imagePath)
                    .into(productViewHolder.mBinding.imageView);
        }else{

        }

        RxUtils.clicks(productViewHolder.mBinding.getRoot())
                .subscribe(view -> {
                    if(mListener != null){
                        mListener.launchProductInfo(product.getId());
                    }
                });
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

    public interface DashboardProductListener {
        void launchProductInfo(String productId);

    }
}
