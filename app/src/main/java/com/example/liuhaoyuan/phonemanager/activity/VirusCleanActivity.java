package com.example.liuhaoyuan.phonemanager.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.AntiVirusDbUtils;
import com.example.liuhaoyuan.phonemanager.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

public class VirusCleanActivity extends AppCompatActivity {

    private TextView mScanningTextView;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private ImageView mScanningImageView;
    private int progress=0;
    private final int SCANNING=1;
    private final int FINISH=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANNING:
                    ScanInfo scanInfo= (ScanInfo) msg.obj;
                    mScanningTextView.setText(scanInfo.name);
                    TextView textView=new TextView(getApplicationContext());
                    textView.setTextSize(18);
                    if (scanInfo.isVirus){
                        textView.setText("发现病毒："+scanInfo.name);
                        textView.setTextColor(Color.RED);
                    }else{
                        textView.setText("扫描完成："+scanInfo.name);
                        textView.setTextColor(Color.BLACK);
                    }
                    mLinearLayout.addView(textView,0);
                    break;
                case FINISH:
                    mScanningImageView.clearAnimation();
                    mScanningTextView.setText("扫描完成");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_clean);
        initView();
        initAnimation();
        initData();
    }

    private void initView() {
        mScanningImageView = (ImageView) findViewById(R.id.iv_is_scanning);
        mScanningTextView = (TextView) findViewById(R.id.tv_scanning);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_scanning);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_scanniing_list);
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setFillAfter(true);
        mScanningImageView.startAnimation(rotateAnimation);
    }

    private void initData(){
        final Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                ArrayList<String> virusList = AntiVirusDbUtils.getAll();
                PackageManager packageManager=getPackageManager();

                ArrayList<ScanInfo> virusInfos=new ArrayList<>();
                ArrayList<ScanInfo> allInfos=new ArrayList<>();
                List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                mProgressBar.setMax(packages.size());
                for (PackageInfo aPackage : packages) {
                    Signature[] signatures = aPackage.signatures;
                    Signature signature = signatures[0];
                    String s = signature.toCharsString();
                    String encode = MD5Utils.encode(s);

                    ScanInfo scanInfo=new ScanInfo();
                    if (virusList.contains(encode)){
                        scanInfo.isVirus=true;
                        virusInfos.add(scanInfo);
                    }else {
                        scanInfo.isVirus=false;
                    }
                    scanInfo.name=aPackage.applicationInfo.loadLabel(packageManager).toString();
                    scanInfo.packageName=aPackage.packageName;
                    allInfos.add(scanInfo);
                    mProgressBar.setProgress(progress++);

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message message = handler.obtainMessage();
                    message.what=SCANNING;
                    message.obj=scanInfo;
                    handler.sendMessage(message);
                }
                Message message = handler.obtainMessage();
                message.what=FINISH;
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private class ScanInfo{
        public String name;
        public String packageName;
        public boolean isVirus;
    }
}
