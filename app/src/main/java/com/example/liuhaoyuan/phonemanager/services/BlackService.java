package com.example.liuhaoyuan.phonemanager.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.liuhaoyuan.phonemanager.utils.BlackListDbUtils;

import java.lang.reflect.Method;

public class BlackService extends Service {

    private SmsReceiver mSmsReceiver;
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener mPhoneStateListener;
    private MyObserver mObserver;

    public BlackService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        register();
        initPhoneListener();
    }

    private void initPhoneListener() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(10000);
        mSmsReceiver = new SmsReceiver();
        registerReceiver(mSmsReceiver, intentFilter);
    }

    private void endCall(String incomingNumber) {
        BlackListDbUtils blackListDbUtils = BlackListDbUtils.getInstance(this);
        int type = blackListDbUtils.getType(incomingNumber);
        if (type == 0 || type == 1) {
            try {
                Class<?> aClass = Class.forName("android.os.ServiceManager");
                Method method = aClass.getMethod("getService", String.class);
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();

                mObserver = new MyObserver(new Handler(),incomingNumber);
                getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),true,
                        mObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSmsReceiver != null) {
            unregisterReceiver(mSmsReceiver);
        }

        if (mTelephonyManager!=null && mPhoneStateListener!=null){
            mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }

        if (mObserver!=null){
            getContentResolver().unregisterContentObserver(mObserver);
        }
    }

    class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            if (objects != null) {
                for (Object object : objects) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                    String address = smsMessage.getOriginatingAddress();
                    BlackListDbUtils mBlackListDbUtils = BlackListDbUtils.getInstance(context);
                    int type = mBlackListDbUtils.getType(address);
                    if (type == 0 || type == 2) {
                        abortBroadcast();
                    }
                }
            }
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    class MyObserver extends ContentObserver {

        private String phone;

        MyObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{phone});
            super.onChange(selfChange);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
