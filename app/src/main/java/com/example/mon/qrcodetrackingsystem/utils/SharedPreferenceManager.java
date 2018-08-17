package com.example.mon.qrcodetrackingsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Item;
import com.example.mon.qrcodetrackingsystem.modules.dashboard.objectmodel.Product;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceManager {
    public static final String SHARED_PREFERENCES_KEY = "com.example.mon.qrcodetrackingsystem";

    private static SharedPreferenceManager mInstance = null;
    private SharedPreferences mSharedPreferences;

    private SharedPreferenceManager(Context context) {

        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance(Context context) {

        synchronized (SharedPreferenceManager.class) {
            if (mInstance == null) {
                mInstance = new SharedPreferenceManager(context);
            }
            return mInstance;
        }
    }

    private void storeArrayInSharedPreferences(String key, List content) {
        Gson gson = new Gson();
        String json = gson.toJson(content);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, json).apply();
    }

    private void storeStringInSharedPreferences(String key, String content) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, content).apply();
    }


    private void removeKeyFromSharedPreferences(String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key).apply();
    }

    private String getStringFromSharedPreferences(String key) {
        return mSharedPreferences.getString(key, "");
    }

    private List<Item> getItemArrayFromSharedPreferences(String key) {
        mSharedPreferences.getString(key, null);
        String json = mSharedPreferences.getString(key, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Item>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private List<Product> getProductArrayFromSharedPreferences(String key) {
        mSharedPreferences.getString(key, null);
        String json = mSharedPreferences.getString(key, "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(json, type);


    }

    public void storeCurrentUserId(String token) {
        storeStringInSharedPreferences(GeneralManager.USER_ID_KEY, token);
    }

    public void removeCurrentUserId() {
        removeKeyFromSharedPreferences(GeneralManager.USER_ID_KEY);
    }

    public String getCurrentUserId() {
        return getStringFromSharedPreferences(GeneralManager.USER_ID_KEY);
    }

    public void removeDeliveryItem(String itemId) {
        List<Item> deliveryItemList = new ArrayList<>();
        if (getDeliveryItem() != null) {
            deliveryItemList = getDeliveryItem();
            for (int i = 0; i < deliveryItemList.size(); i++) {
                if (deliveryItemList.get(i).getId().equals(itemId)) {
                    deliveryItemList.remove(i);
                }
            }
        }
        storeArrayInSharedPreferences(GeneralManager.DELIVERY_ITEM_LIST_KEY, deliveryItemList);
    }

    public void removeDeliveryProduct(String productId) {
        List<Item> deliveryProductList = new ArrayList<>();
        if (getDeliveryItem() != null) {
            deliveryProductList = getDeliveryItem();
            for (int i = 0; i < deliveryProductList.size(); i++) {
                if (deliveryProductList.get(i).getId().equals(productId)) {
                    deliveryProductList.remove(i);
                }
            }
        }
        storeArrayInSharedPreferences(GeneralManager.DELIVERY_PRODUCT_LIST_KEY, deliveryProductList);
    }

    public void storeDeliveryItem(Item item) {
        List<Item> deliveryItemList = new ArrayList<>();
        if (getDeliveryItem() != null) {
            deliveryItemList = getDeliveryItem();
        }
        deliveryItemList.add(item);
        storeArrayInSharedPreferences(GeneralManager.DELIVERY_ITEM_LIST_KEY, deliveryItemList);
    }

    public void storeDeliveryProduct(Product product) {
        List<Product> deliveryItemProductList = new ArrayList<>();
        if (getDeliveryProduct() != null) {
            deliveryItemProductList = getDeliveryProduct();
        }
        deliveryItemProductList.add(product);
        storeArrayInSharedPreferences(GeneralManager.DELIVERY_PRODUCT_LIST_KEY, deliveryItemProductList);
    }

    public List<Item> getDeliveryItem() {
        return getItemArrayFromSharedPreferences(GeneralManager.DELIVERY_ITEM_LIST_KEY);
    }

    public List<Product> getDeliveryProduct() {
        return getProductArrayFromSharedPreferences(GeneralManager.DELIVERY_PRODUCT_LIST_KEY);
    }
}
