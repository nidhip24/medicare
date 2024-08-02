package com.og.medicare.cache;

import java.util.HashMap;

public class AppCache {

    private static AppCache instance;

    private static HashMap<String, String[]> cache = new HashMap<>();

    public static AppCache getInstance() {
        if (instance == null)
            instance = new AppCache();
        return instance;
    }

    public void setCache(String key, String[] value) {
        cache.put(key, value);
    }

    public String[] getCache(String key) {
        return cache.get(key);
    }
}
