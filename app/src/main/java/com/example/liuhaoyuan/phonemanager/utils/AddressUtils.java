package com.example.liuhaoyuan.phonemanager.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressUtils {

	public static final String PATH = "data/data/com.example.liuhaoyuan.phonemanager/files/address.db";

	public static String getAddress(String phoneNumber) {
		String address="未知号码";
		if (phoneNumber.length()<7) {
			return address;
		}
		SQLiteDatabase database= SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{phoneNumber.substring(0,7)});
		if (cursor.moveToNext()) {
			address=cursor.getString(0);
		}
		return address;
	}
}
