package com.example.liuhaoyuan.phonemanager.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.example.liuhaoyuan.phonemanager.activity.WatchDogActivity;
import com.example.liuhaoyuan.phonemanager.utils.AppLockDbUtils;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService extends Service {

    private boolean watching;
    private AppLockDbUtils mDbUtils;
    private ArrayList<String> mPackageNameList;
    private InnerReceiver mInnerReceiver;
    private String mSkipPackageName;
    private InnerObserver mInnerObserver;

    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        watching = true;
        mDbUtils = AppLockDbUtils.getInstance(this);
        watch();

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP_WATCH");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,intentFilter);

        mInnerObserver = new InnerObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://lockapp/change"),true, mInnerObserver);
    }

    private void watch() {
        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                mPackageNameList = mDbUtils.getAll();
                while(watching){
                    ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(0);
                    String packageName = taskInfo.topActivity.getPackageName();
                    if (mPackageNameList.contains(packageName)){
                        if (!TextUtils.equals(packageName,mSkipPackageName)){
                            Intent intent=new Intent(getApplicationContext(), WatchDogActivity.class);
                            //在服务中开启activity，防止没有任务栈导致无法开启,同时清空任务栈，防止开启的是上一个APP拦截activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(ConstantValue.PACKAGE_NAME,packageName);
                            startActivity(intent);
                        }
                    }
                }


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        watching=false;
        if (mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }

        if (mInnerObserver!=null){
            getContentResolver().unregisterContentObserver(mInnerObserver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackageName = intent.getStringExtra(ConstantValue.PACKAGE_NAME);
        }
    }

    private class InnerObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public InnerObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Thread thread=new Thread(){
                @Override
                public void run() {
                    super.run();
                    mPackageNameList = mDbUtils.getAll();
                    mSkipPackageName="";
                }
            };
            thread.start();
        }
    }
}
