package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.liuhaoyuan.phonemanager.R;

public class Setup1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    public void nextPage(View view){

        Intent intent=new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }
}
