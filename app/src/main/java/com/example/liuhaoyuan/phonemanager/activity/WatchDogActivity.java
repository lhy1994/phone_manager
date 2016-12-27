package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;

public class WatchDogActivity extends AppCompatActivity {

    private TextView nameTextView;
    private ImageView appIconImageView;
    private EditText passEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dog);
        initView();
        initData();
    }

    private void initView() {
        nameTextView = (TextView) findViewById(R.id.tv_watch_app_name);
        appIconImageView = (ImageView) findViewById(R.id.iv_watch_icon);
        passEditText = (EditText) findViewById(R.id.et_watch);
        submitButton = (Button) findViewById(R.id.btn_watch_submit);
    }

    private void initData(){
        final String packageName = getIntent().getStringExtra(ConstantValue.PACKAGE_NAME);
        PackageManager packageManager=getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            nameTextView.setText(applicationInfo.loadLabel(packageManager));
            appIconImageView.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passEditText.getText().toString();
                if (TextUtils.isEmpty(pass)){
                    Toast.makeText(getApplicationContext(),"密码为空!",Toast.LENGTH_SHORT).show();
                }else {
                    if (pass.equals("123")){
                        Intent intent=new Intent("android.intent.action.SKIP_WATCH");
                        intent.putExtra(ConstantValue.PACKAGE_NAME,packageName);
                        sendBroadcast(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"密码错误！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
