package com.example.liuhaoyuan.phonemanager;

import android.test.AndroidTestCase;

import com.example.liuhaoyuan.phonemanager.utils.AddressUtils;

/**
 * Created by liuhaoyuan on 2016/12/17.
 */

public class Test extends AndroidTestCase {
    public void get(){
        AddressUtils.getAddress("110");
    }
}
