package com.example.liuhaoyuan.phonemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liuhaoyuan on 2016/12/7.
 */

public class SpUtils {
    private static SharedPreferences mSp;

    public static void putBoolean(Context context, String name, String key, boolean value) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        mSp.edit().putBoolean(key,value).commit();
    }

    public static boolean getBoolean(Context context,String name,String key,boolean defaultValue){
        if (mSp==null){
            mSp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        }
        return mSp.getBoolean(key,defaultValue);
    }

    public static void putString(Context context, String name, String key, String value) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        mSp.edit().putString(key,value).commit();
    }

    public static String getString(Context context,String name,String key,String defaultValue){
        if (mSp==null){
            mSp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        }
        return mSp.getString(key,defaultValue);
    }

    public static void remove(Context context,String name,String key){
        if (mSp==null){
            mSp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        }
        mSp.edit().remove(key).commit();
    }
}
