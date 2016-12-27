package com.example.liuhaoyuan.phonemanager.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.bean.Contact;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Contact> contacts;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ContactAdapter adapter = new ContactAdapter();
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String phoneNumber = contacts.get(position).phone;
                    Intent intent=new Intent();
                    intent.putExtra("phone", phoneNumber);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        listView = (ListView) findViewById(R.id.lv_contacts);
        initData();
    }

    private void initData(){
        contacts = new ArrayList<>();
        contacts.clear();

        Thread thread=new Thread(){
            @Override
            public void run() {
                ContentResolver resolver= getContentResolver();
                Cursor cursor = resolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), new String[]{"contact_id"}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()){
                        String id = cursor.getString(0);
                        Cursor query = resolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
                        Contact contact=new Contact();
                        if (query!=null){
                            while (query.moveToNext()){
                                String data=query.getString(0);
                                String type=query.getString(1);

                                if (type.equals("vnd.android.cursor.item/phone_v2")){
                                    contact.phone=data;
                                }else if (type.equals("vnd.android.cursor.item/name")){
                                    contact.name=data;
                                }
                            }
                            if (!TextUtils.isEmpty(contact.name) && !TextUtils.isEmpty(contact.phone)){
                                contacts.add(contact);
                            }
                            query.close();
                        }
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(1);
            }
        };
        thread.start();
    }

    class ContactAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=View.inflate(getApplicationContext(),R.layout.item_contacts,null);
            }

            ViewHolder holder=ViewHolder.getInstance(convertView);
            holder.name.setText(contacts.get(position).name);
            holder.phone.setText(contacts.get(position).phone);

            return convertView;
        }
    }

    static class ViewHolder{
        TextView name,phone;
        ViewHolder(View convertView){
            name= (TextView) convertView.findViewById(R.id.tv_contacts_name);
            phone= (TextView) convertView.findViewById(R.id.tv_contacts_phone);
        }

        static ViewHolder getInstance(View convertView){
            ViewHolder holder= (ViewHolder) convertView.getTag();
            if (holder==null){
                holder=new ViewHolder(convertView);
            }
            convertView.setTag(holder);
            return holder;
        }
    }
}
