package com.example.liuhaoyuan.phonemanager.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.receiver.MyWidgetReceiver;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

    private Timer mTimer;
    private InnerReceiver mInnerReceiver;

    public UpdateWidgetService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,intentFilter);
        startTimer();
    }

    private void startTimer(){
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateWidget();
            }
        },0,1000);
    }

    private void cancelTimer(){
        if (mTimer!=null){
            mTimer.cancel();
        }
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.appwidget);
        remoteViews.setTextViewText(R.id.tv_widget_process,"进程数 "+ PhoneUtils.getProcessCount(this));
        long ramSpace = PhoneUtils.getAvailRamSpace(this);
        String space = Formatter.formatFileSize(this, ramSpace);
        remoteViews.setTextViewText(R.id.tv_widget_mem,"剩余内存 "+space);

        Intent intent=new Intent("android.intent.action.KILL_PROCESS");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_widget,pendingIntent);

        ComponentName componentName=new ComponentName(this, MyWidgetReceiver.class);
        appWidgetManager.updateAppWidget(componentName,remoteViews);
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
    }

    class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                startTimer();
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                cancelTimer();
            }
        }
    }
}
