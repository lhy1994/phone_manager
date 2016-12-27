package com.example.liuhaoyuan.phonemanager.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.bean.AppInfo;
import com.example.liuhaoyuan.phonemanager.db.AppLockHelper;
import com.example.liuhaoyuan.phonemanager.utils.AppLockDbUtils;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;

import java.util.ArrayList;
import java.util.TreeMap;

public class AppLockActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<AppInfo> mLockList;
    private ArrayList<AppInfo> mUnlockList;
    private ListView mLockListView;
    private ListView mUnlockListView;
    private LinearLayout mLockLayout;
    private LinearLayout mUnlockLayout;
    private TextView mLockTextView;
    private TextView mUnlockTextView;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mLockAdapter = new AppLockAdapter(true);
            mUnlockAdapter = new AppLockAdapter(false);
            mLockListView.setAdapter(mLockAdapter);
            mUnlockListView.setAdapter(mUnlockAdapter);
            updateCount();

        }
    };
    private AppLockDbUtils mDbUtils;
    private AppLockAdapter mLockAdapter;
    private AppLockAdapter mUnlockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initView();
        initData();
    }

    private void initView() {
        Button lockButton= (Button) findViewById(R.id.btn_lock);
        Button unlockButton= (Button) findViewById(R.id.btn_unlock);
        mLockLayout = (LinearLayout) findViewById(R.id.ll_lock);
        mUnlockLayout = (LinearLayout) findViewById(R.id.ll_unlock);
        mLockTextView = (TextView) findViewById(R.id.tv_lock);
        mUnlockTextView = (TextView) findViewById(R.id.tv_unlock);
        mLockListView = (ListView) findViewById(R.id.lv_lock_app);
        mUnlockListView = (ListView) findViewById(R.id.lv_unlock_app);

        lockButton.setOnClickListener(this);
        unlockButton.setOnClickListener(this);
    }

    private void initData(){
        Thread thread=new Thread() {
            @Override
            public void run() {
                super.run();
                ArrayList<AppInfo> appInfoList = PhoneUtils.getAppInfoList(AppLockActivity.this);
                mDbUtils = AppLockDbUtils.getInstance(AppLockActivity.this);
                ArrayList<String> allPackageNames = mDbUtils.getAll();
                mLockList = new ArrayList<>();
                mUnlockList = new ArrayList<>();
                for (AppInfo appInfo : appInfoList) {
                    if (allPackageNames.contains(appInfo.packageName)){
                        mLockList.add(appInfo);
                    }else {
                        mUnlockList.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(1);
            }
        };
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_lock:
                mLockLayout.setVisibility(View.VISIBLE);
                mUnlockLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_unlock:
                mUnlockLayout.setVisibility(View.VISIBLE);
                mLockLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public TranslateAnimation initAnimation(){
        TranslateAnimation translateAnimation=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,1,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
        translateAnimation.setDuration(500);
        return translateAnimation;
    }

    private void updateCount(){
        mLockTextView.setText("已加锁应用："+mLockList.size());
        mUnlockTextView.setText("未加锁应用："+mUnlockList.size());
    }

    class AppLockAdapter extends BaseAdapter{

        private boolean isLock;

        AppLockAdapter(boolean isLock) {
            this.isLock = isLock;
        }


        @Override
        public int getCount() {
            if (isLock){
                return mLockList.size();
            }else {
                return mUnlockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock){
                return mLockList.get(position);
            }else {
                return mUnlockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=View.inflate(getApplicationContext(),R.layout.item_lock_app,null);
            }
            ViewHolder holder=ViewHolder.getInstance(convertView);
            holder.icon.setImageDrawable(getItem(position).icon);
            holder.name.setText(getItem(position).name);
            if (isLock) {
                holder.isLock.setImageResource(R.drawable.lock);
            }else {
                holder.isLock.setImageResource(R.drawable.unlock_24);
            }

            final View animView=convertView;
            final AppInfo appInfo=getItem(position);
            holder.isLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation animation = initAnimation();
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock){
                                mLockList.remove(appInfo);
                                mUnlockList.add(appInfo);
                                mDbUtils.delete(appInfo.packageName);
                                mUnlockAdapter.notifyDataSetChanged();
                            }else {
                                mUnlockList.remove(appInfo);
                                mLockList.add(appInfo);
                                mDbUtils.insert(appInfo.packageName);
                                mLockAdapter.notifyDataSetChanged();
                            }
                            notifyDataSetChanged();
                            updateCount();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    animView.startAnimation(animation);
                }
            });

            return convertView;
        }
    }

    private static class ViewHolder{
        private ImageView icon;
        private TextView name;
        private ImageView isLock;
        ViewHolder (View convertView){
            icon= (ImageView) convertView.findViewById(R.id.iv_lock_app);
            name= (TextView) convertView.findViewById(R.id.tv_lock_app);
            isLock= (ImageView) convertView.findViewById(R.id.iv_islock);
        }

        public static ViewHolder getInstance(View convertView){
            ViewHolder holder= (ViewHolder) convertView.getTag();
            if (holder==null){
                holder=new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
