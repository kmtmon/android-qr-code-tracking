package com.example.mon.qrcodetrackingsystem.manager;

import java.util.ArrayList;
import java.util.List;

public class ItemStatusManager {

    public static String ORDERED_FROM_SUPPLIER = "Ordered from supplier";
    public static String IN_WAREHOUSE = "In warehouse";
    public static String OUT_FOR_DELIVERY = "Out for delivery";
    public static String DELIVERED = "Delivered";

    public static List<String> getStatusArray(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(ORDERED_FROM_SUPPLIER);
        list.add(IN_WAREHOUSE);
        list.add(OUT_FOR_DELIVERY);
        list.add(DELIVERED);
        return list;
    }
}
