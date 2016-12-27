package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;
import com.example.liuhaoyuan.phonemanager.views.SettingItemView;

public class Setup2Activity extends AppCompatActivity {

    private SettingItemView settingItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initView();
        initData();
    }

    private void initView() {
        settingItemView = (SettingItemView) findViewById(R.id.setting_setup2);
        String simNumber = SpUtils.getString(this, ConstantValue.CONFIG, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(simNumber)) {
            settingItemView.setChecked(false);
        } else {
            settingItemView.setChecked(true);
        }
    }

    private void initData() {
        settingItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingItemView.isChecked()) {
                    settingItemView.setChecked(false);
                    SpUtils.remove(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SIM_NUMBER);
                } else {
                    String simNumber = PhoneUtils.getSimNumber(getApplicationContext());
                    if (simNumber == null) {
                        Toast.makeText(getApplicationContext(), "SIM卡找不到,无法绑定", Toast.LENGTH_SHORT).show();
                    } else {
                        settingItemView.setChecked(true);
                        PhoneUtils.saveSimNumber(getApplicationContext(), simNumber);
                    }
                }
            }
        });
    }

    public void previousPage(View view) {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    public void nextPage(View view) {
        String sim = SpUtils.getString(this, ConstantValue.CONFIG, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "请先绑定SIM卡", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in, R.anim.next_out);

        }

    }
}
