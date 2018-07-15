package com.example.mon.qrcodetrackingsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

    public void storeCurrentUserId(String token) {
        storeStringInSharedPreferences(GeneralManager.USER_ID_KEY, token);
    }

    public void removeCurrentUserId() {
        removeKeyFromSharedPreferences(GeneralManager.USER_ID_KEY);
    }

    public String getCurrentUserId() {
        return getStringFromSharedPreferences(GeneralManager.USER_ID_KEY);
    }
}
