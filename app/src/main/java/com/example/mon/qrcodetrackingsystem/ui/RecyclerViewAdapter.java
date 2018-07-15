package com.example.mon.qrcodetrackingsystem.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.mon.qrcodetrackingsystem.databinding.ItemRecyclerViewBinding;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> objectList;
    private RecyclerViewAdapterListener mListener;

    public RecyclerViewAdapter(List<Object> objectList, RecyclerViewAdapterListener mListener) {
        this.objectList = objectList;
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        ItemRecyclerViewBinding agentFinderBinding = ItemRecyclerViewBinding.inflate(inflater, parent, false);
        viewHolder = new RecyclerViewHolder(agentFinderBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = objectList.get(position);


        RecyclerViewHolder productViewHolder = (RecyclerViewHolder)holder;

        if(object instanceof Product){
            Product product = (Product) object;
            productViewHolder.mBinding.title.setText(product.getName());
        }

        if(object instanceof String){
            productViewHolder.mBinding.title.setText((String)object);
        }

        RxUtils.clicks(productViewHolder.mBinding.getRoot())
                .subscribe(view -> {
                    if(mListener != null){
                        mListener.clickOnItem(object);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ItemRecyclerViewBinding mBinding;

        public RecyclerViewHolder(ItemRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }

    public interface RecyclerViewAdapterListener{
        void clickOnItem(Object object);
    }
}
