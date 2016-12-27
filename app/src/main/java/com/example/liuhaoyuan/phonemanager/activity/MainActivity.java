package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.MD5Utils;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] names = new String[]{
            "手机防盗", "通信卫士",
            "软件管理", "进程管理",
            "流量统计", "手机杀毒",
            "缓存清理", "高级工具",
            "设置中心"};
    private int[] imagesIds = new int[]{
            R.drawable.home_safe,
            R.drawable.home_callmsgsafe,
            R.drawable.home_apps,
            R.drawable.home_taskmanager,
            R.drawable.home_netmanager,
            R.drawable.home_trojan,
            R.drawable.home_sysoptimize,
            R.drawable.home_tools,
            R.drawable.home_settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gv_main);
    }

    private void initData() {
        gridView.setAdapter(new MyGridAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String safePwd = SpUtils.getString(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SAFE_PASSWORD, "");
                        if (TextUtils.isEmpty(safePwd)) {
                            showSettingDialog();
                        } else {
                            showPassWordDialog();
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(getApplicationContext(), BlackListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(),ProgressManagerActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(),VirusCleanActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(),CacheClearActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(), AdvanceToolsActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                }
            }
        });
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.dialog_setpwd, null);
        AppCompatButton confirm = (AppCompatButton) view.findViewById(R.id.btn_setpwd_confirm);
        AppCompatButton cancel = (AppCompatButton) view.findViewById(R.id.btn_setpwd_cancel);
        builder.setView(view);
        final AlertDialog dialog = builder.create();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText first = (EditText) view.findViewById(R.id.et_setpwd_first);
                EditText second = (EditText) view.findViewById(R.id.et_setpwd_second);
                String password1 = first.getText().toString();
                String password2 = second.getText().toString();
                if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.equals(password1, password2)) {
                        startActivity(new Intent(getApplicationContext(), PhoneGuardActivity.class));
                        SpUtils.putString(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SAFE_PASSWORD, MD5Utils.encode(password1));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showPassWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.dialog_password, null);
        AppCompatButton confirm = (AppCompatButton) view.findViewById(R.id.btn_pwd_confirm);
        AppCompatButton cancel = (AppCompatButton) view.findViewById(R.id.btn_pwd_cancel);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = SpUtils.getString(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SAFE_PASSWORD, "");
                EditText editText = (EditText) view.findViewById(R.id.et_pwd);
                String input = editText.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String s = MD5Utils.encode(input);
                    if (TextUtils.equals(s, pwd)) {
                        startActivity(new Intent(getApplicationContext(), PhoneGuardActivity.class));
                        SpUtils.putString(getApplicationContext(), ConstantValue.CONFIG, ConstantValue.SAFE_PASSWORD, s);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class MyGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return imagesIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_main, null);
            }
            MainViewHolder holder = MainViewHolder.getInstance(convertView);
            holder.icon.setImageResource(imagesIds[position]);
            holder.name.setText(names[position]);
            return convertView;
        }
    }

    static class MainViewHolder {
        private TextView name;
        private ImageView icon;

        MainViewHolder(View convertView) {
            name = (TextView) convertView.findViewById(R.id.tv_item_main);
            icon = (ImageView) convertView.findViewById(R.id.iv_item_main);
        }

        static MainViewHolder getInstance(View convertView) {
            MainViewHolder holder = (MainViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new MainViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
