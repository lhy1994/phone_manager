package com.example.liuhaoyuan.phonemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
        
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String simNumber = SpUtils.getString(context, ConstantValue.CONFIG, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(simNumber)){
            String CurrSimNumber = PhoneUtils.getSimNumber(context);
            if (!TextUtils.equals(simNumber,CurrSimNumber)){
                String safeNumber = SpUtils.getString(context, ConstantValue.CONFIG, ConstantValue.SAFE_NUMBER, "");
                if (TextUtils.isEmpty(safeNumber)){
                    Log.e("test","no safe number");
                }else {
                    PhoneUtils.sendSMS(safeNumber,"sim number changed !");
                }
            }
        }else {
            Log.e("test","no SIM number");
        }
    }
}
