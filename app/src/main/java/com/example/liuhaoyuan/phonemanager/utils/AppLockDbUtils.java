package com.example.liuhaoyuan.phonemanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.liuhaoyuan.phonemanager.db.AppLockHelper;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/12/22.
 */

public class AppLockDbUtils {
    private final AppLockHelper mHelper;
    private Context context;

    private AppLockDbUtils(Context context) {
        mHelper = new AppLockHelper(context);
        this.context=context;
    }

    private static AppLockDbUtils instance = null;

    public synchronized static AppLockDbUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AppLockDbUtils(context);
        }
        return instance;
    }

    public void insert(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("package_name", packageName);
        database.insert("app_lock", null, values);

        context.getContentResolver().notifyChange(Uri.parse("content://lockapp/change"),null);
        database.close();
    }

    public void delete(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete("app_lock", "package_name=?", new String[]{packageName});

        context.getContentResolver().notifyChange(Uri.parse("content://lockapp/change"),null);
        database.close();
    }

    public ArrayList<String> getAll() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("app_lock", new String[]{"package_name"}, null, null, null, null, null);
        ArrayList<String> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                list.add(name);
            }
            cursor.close();
        }
        database.close();
        return list;
    }
}
