package com.example.liuhaoyuan.phonemanager.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.bean.BlackNumberInfo;
import com.example.liuhaoyuan.phonemanager.utils.BlackListDbUtils;

import java.util.ArrayList;

public class BlackListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<BlackNumberInfo> mInfoList;
    private BlackListDbUtils mBlackListDbUtils;
    private BlackListAdapter mAdapter;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter = new BlackListAdapter();
            listView.setAdapter(mAdapter);
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        initView();
        initData();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_blacklist);
        Button addInfo = (Button) findViewById(R.id.btn_blacklist_add);
        addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void initData(){
        mBlackListDbUtils = BlackListDbUtils.getInstance(getApplicationContext());
        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                mInfoList= mBlackListDbUtils.getAll();
                mHandler.sendEmptyMessage(1);
            }
        };
        thread.start();
    }

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.dialog_black,null);
        final EditText editText= (EditText) view.findViewById(R.id.et_black);
        Button confirm= (Button) view.findViewById(R.id.btn_black_confirm);
        Button cancel= (Button) view.findViewById(R.id.btn_black_cancel);
        final RadioGroup radioGroup= (RadioGroup) view.findViewById(R.id.rg_black);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=editText.getText().toString();
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                int type=0;
                switch (checkedRadioButtonId){
                    case R.id.rb_all:
                        type=0;
                        break;
                    case R.id.rb_phone:
                        type=1;
                        break;
                    case R.id.rb_sms:
                        type=2;
                        break;
                }
                mBlackListDbUtils.add(phoneNumber,type);
                BlackNumberInfo info=new BlackNumberInfo();
                info.phone=phoneNumber;
                info.type=type;
                mInfoList.add(info);
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    class BlackListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView==null){
                convertView=View.inflate(getApplicationContext(),R.layout.item_black_number,null);
            }
            final BlackListViewHolder holder=BlackListViewHolder.getInstance(convertView);
            holder.phone.setText(mInfoList.get(position).phone);

            int type=mInfoList.get(position).type;
            String typeName="";
            switch (type){
                case 0:
                    typeName="拦截全部";
                    break;
                case 1:
                    typeName="拦截电话";
                    break;
                case 2:
                    typeName="拦截短信";
                    break;
                default:
                    break;
            }
            holder.type.setText(typeName);

            holder.delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBlackListDbUtils.delete(mInfoList.get(position).phone);
                    mInfoList.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    static class BlackListViewHolder{
        private TextView phone,type,delButton;
        BlackListViewHolder(View convertView){
            phone= (TextView) convertView.findViewById(R.id.tv_black_number);
            type= (TextView) convertView.findViewById(R.id.tv_black_type);
            delButton= (TextView) convertView.findViewById(R.id.btn_black_delete);
        }

        static BlackListViewHolder getInstance(View convertView){
            BlackListViewHolder holder= (BlackListViewHolder) convertView.getTag();
            if (holder==null){
                holder=new BlackListViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
