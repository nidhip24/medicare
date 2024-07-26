package com.og.medicare.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class DataStorage {

    private static DataStorage instance;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public static DataStorage getInstance(Context mContext) {
        if (instance == null)
            instance = new DataStorage(mContext);
        return instance;
    }

    private DataStorage(Context mContext) {
        pref = mContext.getSharedPreferences("data", 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void setString(String key, Set<String> set) {
        editor.putStringSet(key, set);
        editor.commit();
    }

    public Set<String> getString(String key) {
        return pref.getStringSet(key, null);
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getS(String key) {
        return pref.getString(key, null);
    }
}
