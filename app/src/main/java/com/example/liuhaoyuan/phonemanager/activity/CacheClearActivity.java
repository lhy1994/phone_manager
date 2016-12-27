package com.example.liuhaoyuan.phonemanager.activity;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CacheClearActivity extends AppCompatActivity {

    private Button mDelButton;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private PackageManager mPackageManager;
    private final int UPDATE_CACHE_INFO = 100;
    private final int UPDATE_PROGRESS = 101;
    private final int FINISH = 102;
    private final int CLEAR_CACHE=103;
    private int mProgress = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CACHE_INFO:
                    CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    View view = View.inflate(getApplicationContext(), R.layout.item_cache_info, null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_cache_info);
                    TextView nameTextView = (TextView) view.findViewById(R.id.tv_cache_info_name);
                    TextView sizeTextView = (TextView) view.findViewById(R.id.tv_cache_info_size);
                    Button button = (Button) findViewById(R.id.btn_cache_info_delete);
                    imageView.setImageDrawable(cacheInfo.icon);
                    nameTextView.setText(cacheInfo.name);
                    sizeTextView.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Class<?> aClass = null;
//                            try {
//                                aClass = Class.forName("android.content.pm.PackageManager");
//                                Method method = aClass.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
//                                method.invoke()
//                            } catch (ClassNotFoundException e) {
//                                e.printStackTrace();
//                            } catch (NoSuchMethodException e) {
//                                e.printStackTrace();
//                            }
                        }
                    });
                    mLinearLayout.addView(view, 0);
                    break;
                case UPDATE_PROGRESS:
                    mProgress++;
                    mProgressBar.setProgress(mProgress);
                    String name= (String) msg.obj;
                    mTextView.setText(name);
                    break;
                case FINISH:
                    mTextView.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    mLinearLayout.removeAllViews();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initView();
        initData();
    }

    private void initView() {
        mDelButton = (Button) findViewById(R.id.btn_cache);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_cache);
        mTextView = (TextView) findViewById(R.id.tv_cache);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_cache);

        mDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Class<?> aClass = Class.forName("android.content.pm.PackageManager");
                    Method method = aClass.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    method.invoke(mPackageManager,Long.MAX_VALUE,new IPackageDataObserver.Stub(){

                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                            Message message = Message.obtain();
                            message.what=CLEAR_CACHE;
                            handler.sendMessage(message);
                        }
                    });
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mPackageManager = getPackageManager();
                List<PackageInfo> packages = mPackageManager.getInstalledPackages(0);
                mProgressBar.setMax(packages.size());
                for (PackageInfo packageInfo : packages) {
                    String packageName = packageInfo.packageName;
                    getPackageCache(packageName);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        String name = mPackageManager.getApplicationInfo(packageName, 0).loadLabel(mPackageManager).toString();
                        Message message = Message.obtain();
                        message.obj = name;
                        message.what = UPDATE_PROGRESS;
                        handler.sendMessage(message);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Message message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private void getPackageCache(final String packageName) {
        IPackageStatsObserver.Stub statsObserver = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                long cacheSize = pStats.cacheSize;
                if (cacheSize > 0) {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.packageName = packageName;
                    cacheInfo.cacheSize = cacheSize;
                    try {
                        cacheInfo.name = mPackageManager.getApplicationInfo(packageName, 0).loadLabel(mPackageManager).toString();
                        cacheInfo.icon = mPackageManager.getApplicationInfo(packageName, 0).loadIcon(mPackageManager);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.what = UPDATE_CACHE_INFO;
                    message.obj = cacheInfo;
                    handler.sendMessage(message);
                }
            }
        };

        Class<?> aClass = null;
        try {
            aClass = Class.forName("android.content.pm.PackageManager");
            Method method = aClass.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(mPackageManager, packageName, statsObserver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    class CacheInfo {
        public String name;
        public String packageName;
        public long cacheSize;
        public Drawable icon;
    }
}
