package com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel;

/**
 * Created by mon on 12/7/18.
 */

public class ItemLog {

    public String id;
    public String itemId;
    public String userId;
    public long timestamp;
    public String status;
    public String remark;   // this will store comment or location info for in warehouse item


    public ItemLog() {
    }

    public ItemLog(String itemId, String userId, long timestamp, String status, String remark) {
        this.itemId = itemId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.status = status;
        this.remark = remark;
    }


}
