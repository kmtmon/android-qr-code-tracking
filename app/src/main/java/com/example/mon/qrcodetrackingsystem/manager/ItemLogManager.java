package com.example.mon.qrcodetrackingsystem.manager;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.ItemLog;

public class ItemLogManager {
    public static ItemLog createNewItemLog(String itemId, String userId, long timestamp, String status, String remark){
        ItemLog newItemLog = new ItemLog(itemId,userId,timestamp,status,remark);
        return  newItemLog;
    }
}
