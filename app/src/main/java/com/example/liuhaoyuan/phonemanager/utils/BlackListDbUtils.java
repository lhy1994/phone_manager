package com.example.liuhaoyuan.phonemanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.liuhaoyuan.phonemanager.bean.BlackNumberInfo;
import com.example.liuhaoyuan.phonemanager.db.BlackListOpenHelper;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/12/17.
 */

public class BlackListDbUtils {

    private final BlackListOpenHelper mHelper;

    private BlackListDbUtils(Context context){
        mHelper = new BlackListOpenHelper(context);
    }

    private static BlackListDbUtils instance=null;

    public synchronized static BlackListDbUtils getInstance(Context context){
        if (instance==null){
            instance=new BlackListDbUtils(context);
        }
        return instance;
    }

    public void add(String phone,int type){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("phone_number",phone);
        values.put("type",type);
        db.insert("black_list",null,values);
        db.close();
    }

    public void delete(String phone){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete("black_list","phone_number=?",new String[]{phone});
        db.close();
    }

    public void update(String phone,int type){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("type",type);
        db.update("black_list",values,"phone=?",new String[]{phone});
        db.close();
    }

    public ArrayList<BlackNumberInfo> getAll(){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query("black_list", null, null, null, null, null, null);
        ArrayList<BlackNumberInfo> list=new ArrayList<>();
        while (cursor.moveToNext()){
            BlackNumberInfo info=new BlackNumberInfo();
            String phone_number = cursor.getString(cursor.getColumnIndex("phone_number"));
            int type=cursor.getInt(cursor.getColumnIndex("type"));
            info.phone=phone_number;
            info.type=type;
            list.add(info);
        }
        cursor.close();
        db.close();
        return list;
    }

    public int getType(String phoneNumber){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("black_list", new String[]{"type"}, "phone_number=?", new String[]{phoneNumber}, null, null, null);
        int type=0;
        if (cursor.moveToNext()){
            type=cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return type;
    }
}
