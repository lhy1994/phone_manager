package com.example.liuhaoyuan.phonemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liuhaoyuan on 2016/12/17.
 */

public class AppLockHelper extends SQLiteOpenHelper {
    public AppLockHelper(Context context) {
        super(context, "app_lock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table app_lock(_id integer primary key autoincrement ,package_name varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
