package com.example.liuhaoyuan.phonemanager.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by liuhaoyuan on 2016/12/21.
 */

public class CommonnumUtils {
    private static String path = "data/data/com.example.liuhaoyuan.phonemanager/files/commonnum.db";

    public static ArrayList<Group> getGroups() {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        ArrayList<Group> groups = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Group group = new Group();
                group.name=cursor.getString(0);
                group.idx=cursor.getString(1);
                group.children=getChilds(group.idx);
                groups.add(group);
            }
            cursor.close();
        }
        database.close();
        return groups;
    }

    private static ArrayList<Child> getChilds(String idx){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("table" + idx, null, null, null, null, null, null);
        ArrayList<Child> list=new ArrayList<>();
        if (cursor!=null){
            while (cursor.moveToNext()){
                Child child=new Child();
                child.id=cursor.getString(0);
                child.name=cursor.getString(1);
                child.number=cursor.getString(2);
                list.add(child);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public static class Group {
        public String name;
        public String idx;
        public ArrayList<Child> children;
    }

    public static class Child{
        public String name;
        public String id;
        public String number;
    }
}
