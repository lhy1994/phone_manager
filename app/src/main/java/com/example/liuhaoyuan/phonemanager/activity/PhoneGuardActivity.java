package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

public class PhoneGuardActivity extends AppCompatActivity {

    private TextView tvSafeNumber;
    private ImageView ivLock;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean setupOver = SpUtils.getBoolean(this, ConstantValue.CONFIG, ConstantValue.SETUP_OVER, false);
        if (setupOver){
            setContentView(R.layout.activity_phone_guard);
            initView();
        }else {
            Intent intent=new Intent(this,Setup1Activity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        tvSafeNumber = (TextView) findViewById(R.id.tv_guard_number);
        ivLock = (ImageView) findViewById(R.id.iv_guard_lock);
        button = (Button) findViewById(R.id.btn_guard);

        String safeNumber = SpUtils.getString(this, ConstantValue.CONFIG, ConstantValue.SAFE_NUMBER, "");
        if (!TextUtils.isEmpty(safeNumber)){
            tvSafeNumber.setText(safeNumber);
        }

        boolean open=SpUtils.getBoolean(this,ConstantValue.CONFIG,ConstantValue.OPEN_GUARD,false);
        if (open){
            ivLock.setImageResource(R.drawable.lock);
        }else {
            ivLock.setImageResource(R.drawable.unlock_24);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
