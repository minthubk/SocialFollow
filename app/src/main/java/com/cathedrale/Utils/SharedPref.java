package com.cathedrale.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aspire on 12/15/2015.
 */
public class SharedPref {
    android.content.SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    public SharedPref(Context context) {
        // TODO Auto-generated constructor stub
        preferences =context.getSharedPreferences("Cathedraleye", Context.MODE_PRIVATE);
        editor =preferences.edit();
    }

    public  boolean putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public  boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
    public  boolean putString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }
    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public  boolean putLong(String key, long value) {
        editor.putLong(key, value);
        return editor.commit();
    }

    public  long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    public  boolean putInt(String key, int value) {
        editor.putInt(key, value);
        return editor.commit();
    }
    public  int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public  boolean putFloat(String key, float value) {
        editor.putFloat(key, value);
        return editor.commit();
    }
    public  float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }
    public boolean remove(String key){
        return editor.remove(key).commit();

    }
    public boolean clear() {
        // TODO Auto-generated method stub
        return editor.clear().commit();
    }
}
