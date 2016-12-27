package com.example.liuhaoyuan.phonemanager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.activity.BackGroundActivity;

public class RocketService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private View mRocketView;
    private int startX;
    private int startY;
    private int mScreenWidth;
    private int mScreenHeight;
    private WindowManager.LayoutParams params;

    private final int bottomHeight=150;

    public RocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        Display display = mWindowManager.getDefaultDisplay();
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();

        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Rocket");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.TOP + Gravity.LEFT;

        mRocketView = View.inflate(this, R.layout.rocket_view, null);
        ImageView imageView = (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable background = (AnimationDrawable) imageView.getBackground();
        background.start();

        mWindowManager.addView(mRocketView, params);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;

                        params.x = params.x + dx;
                        params.y = params.y + dy;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.x > mScreenWidth) {
                            params.x = mScreenWidth;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.y>mScreenHeight){
                            params.y=mScreenHeight;
                        }

                        mWindowManager.updateViewLayout(mRocketView, params);
                        startX=endX;
                        startY=endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawY()>mScreenHeight-bottomHeight){
                            playAnimation();
                            Intent intent=new Intent(getApplicationContext(), BackGroundActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }

                return true;
            }
        });
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            params.y= (int) msg.obj;
            mWindowManager.updateViewLayout(mRocketView,params);
        }
    };

    private void playAnimation() {
        final Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                int initHeight=mScreenHeight-bottomHeight;
                for (int i=10;i>0;i--){
                    int height=initHeight/10*i;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message message = handler.obtainMessage();
                    message.obj=height;
                    handler.sendMessage(message);
                }
            }
        };
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mWindowManager != null && mRocketView != null) {
            mWindowManager.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
