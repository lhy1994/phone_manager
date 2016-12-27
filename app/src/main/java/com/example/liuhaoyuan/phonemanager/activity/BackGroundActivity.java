package com.example.liuhaoyuan.phonemanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.liuhaoyuan.phonemanager.R;

public class BackGroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_ground);
        ImageView smokeT= (ImageView) findViewById(R.id.iv_smoke_t);
        ImageView smokeM= (ImageView) findViewById(R.id.iv_smoke_m);
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        smokeT.startAnimation(alphaAnimation);
        smokeM.startAnimation(alphaAnimation);
    }
}
