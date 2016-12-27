package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.services.BlackService;
import com.example.liuhaoyuan.phonemanager.services.PhoneAddressService;
import com.example.liuhaoyuan.phonemanager.services.RocketService;
import com.example.liuhaoyuan.phonemanager.services.WatchDogService;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.ServiceUtils;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;
import com.example.liuhaoyuan.phonemanager.views.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private SettingItemView itemUpdate;
    private SettingItemView itemAddress;
    private SettingItemView itemRocket;
    private SettingItemView itemBlack;
    private SettingItemView itemWatchDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initView() {
        itemUpdate = (SettingItemView) findViewById(R.id.setting_update);
        if (SpUtils.getBoolean(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.AUTO_UPDATE, false)) {
            itemUpdate.setChecked(true);
        } else {
            itemUpdate.setChecked(false);
        }

        itemAddress = (SettingItemView) findViewById(R.id.setting_address);
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.liuhaoyuan.phonemanager.services.PhoneAddressService");
        itemAddress.setChecked(isRunning);

        itemRocket = (SettingItemView) findViewById(R.id.setting_rocket);
        isRunning=ServiceUtils.isRunning(this,"com.example.liuhaoyuan.phonemanager.services.RocketService");
        itemRocket.setChecked(isRunning);

        itemBlack = (SettingItemView) findViewById(R.id.setting_black);
        isRunning=ServiceUtils.isRunning(this,"com.example.liuhaoyuan.phonemanager.services.BlackService");
        itemRocket.setChecked(isRunning);

        itemWatchDog = (SettingItemView) findViewById(R.id.setting_watch);
        isRunning=ServiceUtils.isRunning(this,"com.example.liuhaoyuan.phonemanager.services.WatchDogService");
        itemWatchDog.setChecked(isRunning);
    }

    private void initData() {
        itemUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemUpdate.isChecked()) {
                    itemUpdate.setChecked(false);
                    SpUtils.putBoolean(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.AUTO_UPDATE, false);
                } else {
                    itemUpdate.setChecked(true);
                    SpUtils.putBoolean(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.AUTO_UPDATE, true);
                }
            }
        });

        itemAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemAddress.isChecked()) {
                    itemAddress.setChecked(false);
                    SpUtils.putBoolean(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SHOW_ADDRESS, false);
                    stopService(new Intent(getApplicationContext(), PhoneAddressService.class));
                } else {
                    itemAddress.setChecked(true);
                    SpUtils.putBoolean(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SHOW_ADDRESS, true);
                    startService(new Intent(getApplicationContext(), PhoneAddressService.class));
                }
            }
        });

        itemRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemRocket.isChecked()){
                    itemRocket.setChecked(false);
                    stopService(new Intent(getApplicationContext(), RocketService.class));
                }else {
                    itemRocket.setChecked(true);
                    startService(new Intent(getApplicationContext(),RocketService.class));
                }
            }
        });

        itemBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemBlack.isChecked()){
                    itemAddress.setChecked(false);
                    stopService(new Intent(getApplicationContext(), BlackService.class));
                }else {
                    itemBlack.setChecked(true);
                    startService(new Intent(getApplicationContext(),BlackService.class));
                }
            }
        });

        itemWatchDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemWatchDog.isChecked()){
                    itemWatchDog.setChecked(false);
                    stopService(new Intent(getApplicationContext(), WatchDogService.class));
                }else {
                    itemWatchDog.setChecked(true);
                    startService(new Intent(getApplicationContext(),WatchDogService.class));
                }
            }
        });
    }
}
