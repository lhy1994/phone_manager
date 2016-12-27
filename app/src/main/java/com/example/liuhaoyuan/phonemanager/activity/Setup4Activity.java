package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

public class Setup4Activity extends AppCompatActivity {

    private CheckBox checkBox;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        initView();
    }


    private void initView() {
        checkBox = (CheckBox) findViewById(R.id.cb_setup4);
        textView = (TextView) findViewById(R.id.tv_setup4);
        boolean open = SpUtils.getBoolean(this, ConstantValue.CONFIG, ConstantValue.OPEN_GUARD, false);
        if (open) {
            checkBox.setChecked(true);
            textView.setText("手机防护已开启");
        } else {
            checkBox.setChecked(false);
            textView.setText("手机防护未开启");
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textView.setText("手机防护已开启");
                } else {
                    textView.setText("手机防护未开启");
                }
            }
        });

    }

    public void previousPage(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    public void nextPage(View view) {
        SpUtils.putBoolean(this, ConstantValue.CONFIG, ConstantValue.SETUP_OVER, true);
        SpUtils.putBoolean(this, ConstantValue.CONFIG, ConstantValue.OPEN_GUARD, checkBox.isChecked());
        Intent intent = new Intent(this, PhoneGuardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }
}
