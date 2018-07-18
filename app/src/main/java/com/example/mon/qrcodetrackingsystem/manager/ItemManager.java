package com.example.mon.qrcodetrackingsystem.manager;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;

public class ItemManager {
    public static Item createNewItem(String productId){
        Item newItem = new Item();
        newItem.setProductID(productId);
        return  newItem;
    }
}
