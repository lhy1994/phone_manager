package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.bean.AppInfo;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;

import java.util.ArrayList;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView appInfoListView;
    private TextView floatTextView;
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initView();
        initData();
    }

    private void initView() {
        TextView memSpaceTextView = (TextView) findViewById(R.id.tv_mem_space);
        TextView sdSpaceTextView = (TextView) findViewById(R.id.tv_sd_space);
        String memPath = Environment.getDataDirectory().getAbsolutePath();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String memSpace = Formatter.formatFileSize(this, PhoneUtils.getAvailSpace(memPath));
        String sdSpace = Formatter.formatFileSize(this, PhoneUtils.getAvailSpace(sdPath));
        memSpaceTextView.setText("手机存储剩余:" + memSpace);
        sdSpaceTextView.setText("sd卡剩余:" + sdSpace);

        appInfoListView = (ListView) findViewById(R.id.lv_app);
        floatTextView = (TextView) findViewById(R.id.tv_float);
    }

    private void initData() {
        MyTask task = new MyTask();
        task.execute(this);
    }

    private void showPopupWindow(View anchor){
        View view=View.inflate(this,R.layout.popup_app,null);
        Button uninstall= (Button) view.findViewById(R.id.btn_uninstall);
        Button openApp= (Button) view.findViewById(R.id.btn_open_app);
        Button shareApp= (Button) view.findViewById(R.id.btn_share_app);

        uninstall.setOnClickListener(this);
        openApp.setOnClickListener(this);
        shareApp.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        //设置透明背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAsDropDown(anchor,50,-anchor.getHeight());

        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1,0,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);
        AnimationSet animationSet=new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        view.startAnimation(animationSet);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_uninstall:
                if (mAppInfo.isUser){
                    Uri uri=Uri.parse("package:"+mAppInfo.packageName);
                    Intent intent=new Intent(Intent.ACTION_DELETE,uri);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"系统应用不能卸载",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_open_app:
                PackageManager packageManager=getPackageManager();
                Intent intentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.packageName);
                if (intentForPackage!=null){
                    startActivity(intentForPackage);
                }else {
                    Toast.makeText(getApplicationContext(),"此应用不能打开",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_share_app:
                break;
        }
        if (mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }

    class MyTask extends AsyncTask<Context, Object, ArrayList<AppInfo>> {

        @Override
        protected ArrayList<AppInfo> doInBackground(Context... params) {
            return PhoneUtils.getAppInfoList(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            final ArrayList<AppInfo> customList = new ArrayList<>();
            final ArrayList<AppInfo> systemList = new ArrayList<>();
            for (AppInfo appInfo : appInfos) {
                if (appInfo.isUser) {
                    customList.add(appInfo);
                } else {
                    systemList.add(appInfo);
                }
            }
            AppInfoAdapter adapter = new AppInfoAdapter(customList, systemList);
            appInfoListView.setAdapter(adapter);
            appInfoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem<customList.size()+1){
                        floatTextView.setText("用户应用");
                    }else {
                        floatTextView.setText("系统应用");
                    }
                }
            });
            appInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position==0 || position==customList.size()+1){
                        return;
                    }else {
                        if (position<customList.size()+1){
                            mAppInfo = customList.get(position-1);
                        }else {
                            mAppInfo=systemList.get(position-customList.size()-2);
                        }
                        showPopupWindow(view);
                    }
                }
            });
        }
    }

    class AppInfoAdapter extends BaseAdapter {

        private ArrayList<AppInfo> customAppList;
        private ArrayList<AppInfo> systemAppList;

        AppInfoAdapter(ArrayList<AppInfo> customAppList, ArrayList<AppInfo> systemAppList) {
            this.customAppList = customAppList;
            this.systemAppList = systemAppList;
        }

        @Override
        public int getCount() {
            return customAppList.size() + systemAppList.size() + 2;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == customAppList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == customAppList.size() + 1) {
                return null;
            } else {
                if (position < customAppList.size() + 1) {
                    return customAppList.get(position - 1);
                } else {
                    return systemAppList.get(position - customAppList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                View view = View.inflate(getApplicationContext(), R.layout.item_divder, null);
                TextView textView = (TextView) view.findViewById(R.id.tv_divider);
                if (position == 0) {
                    textView.setText("用户应用");
                } else {
                    textView.setText("系统应用");
                }
                return view;
            } else {
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_appinfo, null);
                }
                AppInfoViewHolder holder = AppInfoViewHolder.getInstance(convertView);
                holder.icon.setImageDrawable(getItem(position).icon);
                holder.appName.setText(getItem(position).name);
                if (getItem(position).isSd) {
                    holder.isSd.setText("sd卡应用");
                } else {
                    holder.isSd.setText("手机应用");
                }
                return convertView;
            }
        }
    }

    static class AppInfoViewHolder {
        private ImageView icon;
        private TextView appName;
        private TextView isSd;

        AppInfoViewHolder(View convertView) {
            icon = (ImageView) convertView.findViewById(R.id.iv_app);
            appName = (TextView) convertView.findViewById(R.id.tv_app_name);
            isSd = (TextView) convertView.findViewById(R.id.tv_app_sd);
        }

        static AppInfoViewHolder getInstance(View convertView) {
            AppInfoViewHolder holder = (AppInfoViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new AppInfoViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
