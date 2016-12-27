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

public class AntiVirusDbUtils {
    public static final String PATH = "data/data/com.example.liuhaoyuan.phonemanager/files/antivirus.db";

    public static ArrayList<String> getAll(){
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("datable", new String[]{"md5"}, null, null, null, null, null);
        ArrayList<String> list=new ArrayList<>();
        if (cursor!=null){
            while (cursor.moveToNext()){
                String s=cursor.getString(0);
                list.add(s);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

}
