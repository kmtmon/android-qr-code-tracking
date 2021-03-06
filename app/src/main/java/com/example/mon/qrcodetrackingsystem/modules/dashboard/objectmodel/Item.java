package com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel;

/**
 * Created by mon on 12/7/18.
 */

public class Item {

    public String id;
    public String productID;
    public String status;
    public String remark;
    public String description;
    public double lat;
    public double lng;


    public Item() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getProductID() {
        return productID;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
