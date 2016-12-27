package com.example.liuhaoyuan.phonemanager.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.bean.ProcessInfo;
import com.example.liuhaoyuan.phonemanager.utils.ConstantValue;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;
import com.example.liuhaoyuan.phonemanager.utils.SpUtils;

import java.util.ArrayList;

public class ProgressManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView totalTextView;
    private TextView rmTextView;
    private ListView processListView;
    private Button allButton;
    private Button inverseButton;
    private Button swipeButton;
    private Button settingButton;
    private ProcessInfo mProcessInfo;
    private ProcessListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_manager);
        initView();
        initData();
    }

    private void initView() {
        totalTextView = (TextView) findViewById(R.id.tv_progress_total);
        rmTextView = (TextView) findViewById(R.id.tv_ram);
        processListView = (ListView) findViewById(R.id.lv_progress);
        allButton = (Button) findViewById(R.id.btn_select_all);
        inverseButton = (Button) findViewById(R.id.btn_select_inverse);
        swipeButton = (Button) findViewById(R.id.btn_swipe);
        settingButton = (Button) findViewById(R.id.btn_setting);
    }

    private void initData() {
        updateCountAndSize();
        initListData();
        allButton.setOnClickListener(this);
        inverseButton.setOnClickListener(this);
        swipeButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
    }

    private void updateCountAndSize() {
        int processCount = PhoneUtils.getProcessCount(this);
        totalTextView.setText("进程总数 " + processCount);
        long availRamSpace = PhoneUtils.getAvailRamSpace(this);
        long totalRamSpace = PhoneUtils.getTotalRamSpace();
        String avail = Formatter.formatFileSize(this, availRamSpace);
        String total = Formatter.formatFileSize(this, totalRamSpace);
        rmTextView.setText("内存情况 " + avail + "/" + total);
        Log.e("test",availRamSpace+"///"+totalRamSpace);
    }

    private void initListData() {
        MyTask task = new MyTask();
        task.execute(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_all:
                if (mAdapter != null) {
                    mAdapter.selectAll();
                }
                break;
            case R.id.btn_select_inverse:
                if (mAdapter != null) {
                    mAdapter.selectInverse();
                }
                break;
            case R.id.btn_swipe:
                if (mAdapter != null) {
                    mAdapter.swipeAll();
                }
                break;
            case R.id.btn_setting:
                startActivityForResult(new Intent(this,ProcessSettingActivity.class),1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    class MyTask extends AsyncTask<Context, Object, ArrayList<ProcessInfo>> {

        @Override
        protected ArrayList<ProcessInfo> doInBackground(Context... params) {
            return PhoneUtils.getProcessInfoList(params[0]);
        }

        @Override
        protected void onPostExecute(final ArrayList<ProcessInfo> processInfos) {
            super.onPostExecute(processInfos);
            final ArrayList<ProcessInfo> customList = new ArrayList<>();
            final ArrayList<ProcessInfo> systemList = new ArrayList<>();
            for (ProcessInfo info : processInfos) {
                if (info.isSystem) {
                    systemList.add(info);
                } else {
                    customList.add(info);
                }
            }
            mAdapter = new ProcessListAdapter(getApplicationContext(), customList, systemList);
            processListView.setAdapter(mAdapter);
            processListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!(position == 0 || position == customList.size() + 1)) {
                        if (position < customList.size() + 1) {
                            mProcessInfo = customList.get(position - 1);
                        } else {
                            mProcessInfo = systemList.get(position - customList.size() - 2);
                        }
                        if (!mProcessInfo.packageName.equals(getPackageName())) {
                            mProcessInfo.isChecked = !mProcessInfo.isChecked;
                            CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_process);
                            checkBox.setChecked(mProcessInfo.isChecked);
                        }
                    }
                }
            });
        }
    }

    class ProcessListAdapter extends BaseAdapter {

        private ArrayList<ProcessInfo> customList;
        private ArrayList<ProcessInfo> systemList;
        private Context context;

        ProcessListAdapter(Context context, ArrayList<ProcessInfo> customList, ArrayList<ProcessInfo> systemList) {
            this.customList = customList;
            this.systemList = systemList;
            this.context = context;
        }

        void selectAll() {
            for (ProcessInfo processInfo : customList) {
                processInfo.isChecked = true;
            }
            for (ProcessInfo processInfo : systemList) {
                processInfo.isChecked = true;
            }
            notifyDataSetChanged();
        }

        void selectInverse() {
            for (ProcessInfo processInfo : customList) {
                processInfo.isChecked = !processInfo.isChecked;
            }
            for (ProcessInfo processInfo : systemList) {
                processInfo.isChecked = !processInfo.isChecked;
            }
            notifyDataSetChanged();
        }

        void swipeAll() {
            int count = 0;
            long memSize = 0;
            ArrayList<ProcessInfo> killList = new ArrayList<>();
            for (ProcessInfo processInfo : customList) {
                if (!processInfo.packageName.equals(getPackageName())){
                    if (processInfo.isChecked) {
                        killList.add(processInfo);
                    }
                }
            }
            for (ProcessInfo processInfo : systemList) {
                if (!processInfo.packageName.equals(getPackageName())){
                    if (processInfo.isChecked) {
                        killList.add(processInfo);
                    }
                }
            }

            for (ProcessInfo processInfo : killList) {
                if (customList.contains(processInfo)) {
                    customList.remove(processInfo);
                }
                if (systemList.contains(processInfo)) {
                    systemList.remove(processInfo);
                }
                count++;
                memSize += processInfo.memSize;
                PhoneUtils.killProcess(context, processInfo);
            }
            notifyDataSetChanged();
            String size = Formatter.formatFileSize(context, memSize);
            Toast.makeText(context, "杀死了" + count + "个进程" + "释放了" + size + "空间", Toast.LENGTH_SHORT).show();
            updateCountAndSize();
        }

        @Override
        public int getCount() {
            if (SpUtils.getBoolean(context, ConstantValue.CONFIG,ConstantValue.SHOW_SYSTEM_PROCESS,true)){
                return customList.size() + systemList.size() + 2;
            }else {
                return customList.size()+1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == customList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == customList.size() + 1) {
                return null;
            } else {
                if (position < customList.size() + 1) {
                    return customList.get(position - 1);
                } else {
                    return systemList.get(position - customList.size() - 2);
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
                    textView.setText("用户进程");
                } else {
                    textView.setText("系统进程");
                }
                return view;
            } else {
                if (convertView == null) {
                    convertView = View.inflate(ProgressManagerActivity.this, R.layout.item_process_info, null);
                }
                ProcessInfoViewHolder holder = ProcessInfoViewHolder.getInstance(convertView);
                holder.icon.setImageDrawable(getItem(position).icon);
                holder.processName.setText(getItem(position).name);
                if (getItem(position).isSystem) {
                    holder.isSystem.setText("系统进程");
//                    holder.checkBox.setVisibility(View.GONE);
                } else {
                    holder.isSystem.setText("用户进程");
//                    holder.checkBox.setVisibility(View.VISIBLE);
                }
                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.checkBox.setVisibility(View.GONE);
                } else {
                    holder.checkBox.setVisibility(View.VISIBLE);
                }
                holder.checkBox.setChecked(getItem(position).isChecked);
                return convertView;
            }
        }
    }

    static class ProcessInfoViewHolder {
        private ImageView icon;
        private TextView processName;
        private TextView isSystem;
        private CheckBox checkBox;

        ProcessInfoViewHolder(View convertView) {
            icon = (ImageView) convertView.findViewById(R.id.iv_process_icon);
            processName = (TextView) convertView.findViewById(R.id.tv_process_name);
            isSystem = (TextView) convertView.findViewById(R.id.tv_process_sys);
            checkBox = (CheckBox) convertView.findViewById(R.id.cb_process);
        }

        static ProcessInfoViewHolder getInstance(View convertView) {
            ProcessInfoViewHolder holder = (ProcessInfoViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ProcessInfoViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
