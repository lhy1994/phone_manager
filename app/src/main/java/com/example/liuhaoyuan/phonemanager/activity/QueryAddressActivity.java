package com.example.liuhaoyuan.phonemanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.AddressUtils;

public class QueryAddressActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        initView();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.et_query_number);
        textView = (TextView) findViewById(R.id.tv_query_result);
    }

    public void query(View view){
        String number = editText.getText().toString();
        if (TextUtils.isEmpty(number)){
            Toast.makeText(getApplicationContext(),"输入不能为空",Toast.LENGTH_SHORT).show();
        }else {
            textView.setText(AddressUtils.getAddress(number));
        }
    }
}
