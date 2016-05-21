package com.reactnativenavigation.core.objects;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableMap;

public class JsonObject {

    protected String getString(ReadableMap map, String key) {
        return getString(map, key, null);
    }

    protected String getString(ReadableMap map, String key, String defaultValue) {
        return map.hasKey(key) ? map.getString(key) : defaultValue;
    }

    protected int getInt(ReadableMap map, String key) {
        return map.hasKey(key) ? map.getInt(key) : -1;
    }

    protected boolean getBoolean(ReadableMap map, String key) {
        return map.hasKey(key) && map.getBoolean(key);
    }

    protected ReadableMap getMap(ReadableMap map, String key) {
        return map.hasKey(key) ? map.getMap(key) : null;
    }

    protected Integer getColor(ReadableMap map, String key) {
        return map.hasKey(key) ? Color.parseColor(map.getString(key)) : null;
    }
}
