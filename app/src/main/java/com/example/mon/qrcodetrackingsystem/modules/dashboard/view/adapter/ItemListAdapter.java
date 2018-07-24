package com.example.mon.qrcodetrackingsystem.modules.dashboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mon.qrcodetrackingsystem.databinding.ItemItemBinding;
import com.example.mon.qrcodetrackingsystem.manager.ItemLogManager;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.utils.RxUtils;

import java.util.List;

/**
 * Created by mon on 12/7/18.
 */

public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> mItemList;
    private ItemListAdapterListner mListner;

    public ItemListAdapter(List<Item> mItemList, ItemListAdapterListner listner) {
        this.mItemList = mItemList;
        this.mListner = listner;
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
        ItemViewHolder mViewHolder = (ItemViewHolder) holder;

        Item mItem = mItemList.get(position);

        if (mItem.getId() != null) {
            mViewHolder.mBinding.serialnumber.setText(mItem.getId());

            mViewHolder.mBinding.status.setText("");
            mViewHolder.mBinding.remark.setText("");

            ItemLogManager.getInstance().retrieveLatestItemLog(mItem.getId(), itemLog -> {
                if (itemLog.status != null) {
                    mViewHolder.mBinding.status.setText(itemLog.status);
                }

                if (itemLog.remark != null) {
                    mViewHolder.mBinding.remark.setText(itemLog.remark);
                }
            });
        }

        RxUtils.clicks(mViewHolder.mBinding.getRoot())
                .subscribe(view ->{
                    if(mListner != null){
                        mListner.clickOnItem(mItem);
                    }
                });
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

    public interface ItemListAdapterListner {
        void clickOnItem(Item item);
    }
}
