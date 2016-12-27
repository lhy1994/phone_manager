package com.example.liuhaoyuan.phonemanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;

public class AdvanceToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_tools);
    }

    public void queryAddress(View view){
        startActivity(new Intent(this,QueryAddressActivity.class));
    }

    public void smsBackUp(View view){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("短信备份");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                PhoneUtils.smsBackup(getApplicationContext(), getFilesDir().getAbsolutePath()+"/sms.xml",progressDialog);
                progressDialog.dismiss();
            }
        };
        thread.start();
    }

    public void queryNumber(View view){
        startActivity(new Intent(this,CommonNumberActivity.class));
    }

    public void proLock(View view){
        startActivity(new Intent(this,AppLockActivity.class));
    }


}
