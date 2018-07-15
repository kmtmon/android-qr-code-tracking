package com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Product {

    public String id;
    public String name;
    public String desc;

    public Product() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
