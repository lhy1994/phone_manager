package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.services.LockClearService;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.ServiceUtils;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

public class ProcessSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        init();
    }

    private void init() {
        final CheckBox showSys= (CheckBox) findViewById(R.id.cb_show_sys);
        final CheckBox lockClear= (CheckBox) findViewById(R.id.cb_lock_clear);
        boolean show = SpUtils.getBoolean(this, ConstantValue.CONFIG, ConstantValue.SHOW_SYSTEM_PROCESS, true);
        showSys.setChecked(show);
        boolean running = ServiceUtils.isRunning(this, "com.example.liuhaoyuan.phonemanager.services.LockClearService");
        lockClear.setChecked(running);

        showSys.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SpUtils.putBoolean(getApplicationContext(), ConstantValue.CONFIG,ConstantValue.SHOW_SYSTEM_PROCESS,true);
                }else {
                    SpUtils.putBoolean(getApplicationContext(),ConstantValue.CONFIG,ConstantValue.SHOW_SYSTEM_PROCESS,false);
                }
            }
        });
        lockClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startService(new Intent(getApplicationContext(),LockClearService.class));
                }else {
                    stopService(new Intent(getApplicationContext(),LockClearService.class));
                }
            }
        });
    }
}
