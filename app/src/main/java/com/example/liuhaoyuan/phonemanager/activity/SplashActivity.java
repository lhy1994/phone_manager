package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        playAnimation();
        copyDb("address.db");
        copyDb("commonnum.db");
        copyDb("antivirus.db");
        initShortCut();
    }

    private void playAnimation() {
        AlphaAnimation scaleAnimation= new AlphaAnimation(0.6f,1);
        scaleAnimation.setDuration(3000);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        relativeLayout.startAnimation(scaleAnimation);
    }

    private void initView(){
        textView = (TextView) findViewById(R.id.tv_splash_ver);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_splash);
    }

    private void initData(){
        String versionName = getVersionName();
        if (versionName!=null){
            textView.setText("version "+getVersionName());
        }
    }

    private String getVersionName(){
        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initShortCut(){
        Intent intent=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"phone manager shortcut");

        Intent intent1=new Intent("com.example.liuhaoyuan.phonemanager.MAIN");
        intent1.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent1);
        sendBroadcast(intent);
    }

    private void copyDb(String source) {
        File desFile =new File(getFilesDir(), source);
        if (!desFile.exists()) {
            InputStream inputStream = null;
            FileOutputStream outputStream=null;
            try {
                outputStream = new FileOutputStream(desFile);
                inputStream = getAssets().open(source);
                int len;
                byte[] bs=new byte[1024];
                while((len=inputStream.read(bs))!=-1){
                    outputStream.write(bs, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
