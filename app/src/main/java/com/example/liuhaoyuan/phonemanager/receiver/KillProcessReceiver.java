package com.example.liuhaoyuan.phonemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;

public class KillProcessReceiver extends BroadcastReceiver {
    public KillProcessReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PhoneUtils.killAllProcess(context);
    }
}
