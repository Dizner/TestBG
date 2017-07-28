package com.dlrj.yuanqu.controller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * SharedPreferencesUtil 先给设置上下文，再使用；
 * 一般是设置成Application 中的context
 */
public class SharedPreferencesUtil {

    private static SharedPreferencesUtil sSharedPreferencesUtil = null;

    private SharedPreferencesUtil() {

    }
    synchronized public static SharedPreferencesUtil getInstance() {
        if (sSharedPreferencesUtil == null) {
            sSharedPreferencesUtil = new SharedPreferencesUtil();
        }
        return sSharedPreferencesUtil;
    }

    private Context context;

    public void setContext(Context context) {
        this.context = context;
//        SP.context = context;
    }

    public String getPrefString(final String key, final String defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public void setPrefString(final String key, final String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }

    public boolean getPrefBoolean(final String key, final boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    public boolean hasKey(final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

    public void setPrefBoolean(final String key, final boolean value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).commit();
    }

    public void setPrefInt(final String key, final int value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).commit();
    }

    public void increasePrefInt(final String key) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        increasePrefInt(settings, key);
    }

    public void increasePrefInt(final SharedPreferences sp, final String key) {
        final int v = sp.getInt(key, 0) + 1;
        sp.edit().putInt(key, v).commit();
    }

    public void increasePrefInt(final SharedPreferences sp, final String key,
                                final int increment) {
        final int v = sp.getInt(key, 0) + increment;
        sp.edit().putInt(key, v).commit();
    }

    public int getPrefInt(final String key, final int defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public void setPrefFloat(final String key, final float value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).commit();
    }

    public float getPrefFloat(final String key, final float defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public void setPrefLong(final String key, final long value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).commit();
    }

    public long getPrefLong(final String key, final long defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    public void increasePrefLong(final SharedPreferences sp, final String key,
                                 final long increment) {
        final long v = sp.getLong(key, 0) + increment;
        sp.edit().putLong(key, v).commit();
    }

    public void removePreference(final String key) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(key).commit();
    }

    public void clearPreference(final SharedPreferences p) {
        final Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
