package com.example.liuhaoyuan.phonemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liuhaoyuan on 2016/12/17.
 */

public class BlackListOpenHelper extends SQLiteOpenHelper {
    public BlackListOpenHelper(Context context) {
        super(context, "black_list.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table black_list(_id integer primary key autoincrement ,phone_number varchar(20),type integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
