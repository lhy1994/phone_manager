package com.example.liuhaoyuan.phonemanager.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.AddressUtils;

public class PhoneAddressService extends Service {

    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener mPhoneStateListener;
    private WindowManager mWindowManager;
    private View mToastView;
    private TextView mTextView;
    private OutgoingCallReceiver mOutgoingCallReceiver;

    public PhoneAddressService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        mOutgoingCallReceiver = new OutgoingCallReceiver();
        registerReceiver(mOutgoingCallReceiver,intentFilter);
    }

    class OutgoingCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String phone=getResultData();
            showAddress(phone);
        }
    }

    @Override
    public void onDestroy() {
        if (mTelephonyManager!=null && mPhoneStateListener!=null){
            mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }

        if (mOutgoingCallReceiver!=null){
            unregisterReceiver(mOutgoingCallReceiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    public void showAddress(String address){
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity= Gravity.TOP+Gravity.LEFT;

        mToastView = View.inflate(this, R.layout.toast_view,null);
        mTextView = (TextView) mToastView.findViewById(R.id.tv_toast);
        mWindowManager.addView(mToastView,params);
        queryAddress(address);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                String address= (String) msg.obj;
                mTextView.setText(address);
            }
        }
    };

    private void queryAddress(final String phoneNumber) {
        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                String address = AddressUtils.getAddress(phoneNumber);
                Message message = handler.obtainMessage(1);
                message.obj=address;
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
    class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    if(mToastView!=null){
                        mWindowManager.removeView(mToastView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    showAddress(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

}
