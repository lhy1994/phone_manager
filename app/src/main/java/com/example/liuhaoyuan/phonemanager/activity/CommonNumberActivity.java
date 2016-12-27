package com.example.liuhaoyuan.phonemanager.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.utils.CommonnumUtils;
import com.example.liuhaoyuan.phonemanager.utils.PhoneUtils;

import java.util.ArrayList;

public class CommonNumberActivity extends AppCompatActivity {

    private ArrayList<CommonnumUtils.Group> mGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        init();
    }

    private void init() {
        ExpandableListView listView= (ExpandableListView) findViewById(R.id.elv_number);
        mGroups = CommonnumUtils.getGroups();
        MyExpandableAdapter adapter=new MyExpandableAdapter();
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                PhoneUtils.startCall(CommonNumberActivity.this,mGroups.get(groupPosition).children.get(childPosition).number);
                return false;
            }
        });
    }

    class MyExpandableAdapter extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            return mGroups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroups.get(groupPosition).children.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mGroups.get(groupPosition).children.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView=new TextView(getApplicationContext());
            textView.setText("    "+mGroups.get(groupPosition).name);
            textView.setTextSize(18);
            textView.setTextColor(Color.RED);
            textView.setPadding(10,10,10,10);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view=View.inflate(getApplicationContext(),R.layout.item_number,null);
            TextView number= (TextView) view.findViewById(R.id.tv_number);
            TextView des= (TextView) view.findViewById(R.id.tv_number_des);
            number.setText(mGroups.get(groupPosition).children.get(childPosition).number);
            des.setText(mGroups.get(groupPosition).children.get(childPosition).name);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
