package com.example.liuhaoyuan.phonemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

public class Setup3Activity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initView();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.et_setup3);
        String safeNumber = SpUtils.getString(this, ConstantValue.CONFIG, ConstantValue.SAFE_NUMBER, "");
        if (!TextUtils.isEmpty(safeNumber)){
            editText.setText(safeNumber);
        }

        Button button = (Button) findViewById(R.id.btn_setup3_contacts);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setup3Activity.this,ContactListActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            String phoneNumber=data.getStringExtra("phone");
            editText.setText(phoneNumber);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void previousPage(View view){
        Intent intent=new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
    }

    public void nextPage(View view) {
        String s = editText.getText().toString();
        if (TextUtils.isEmpty(s)){
            Toast.makeText(this,"请选择一个安全号码！",Toast.LENGTH_SHORT).show();
        }else {
            SpUtils.putString(getApplicationContext(),ConstantValue.CONFIG,ConstantValue.SAFE_NUMBER,s);
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in,R.anim.next_out);
        }

    }
}
