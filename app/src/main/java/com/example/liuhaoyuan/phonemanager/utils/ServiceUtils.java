package com.example.liuhaoyuan.phonemanager.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.example.liuhaoyuan.phonemanager.bean.Contact;

import java.util.List;

/**
 * Created by liuhaoyuan on 2016/12/14.
 */

public class ServiceUtils {
    public static boolean isRunning(Context context, String serviceName){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo service : services) {
            if (service.service.getClassName().equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
