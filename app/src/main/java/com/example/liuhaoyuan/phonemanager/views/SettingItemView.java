package com.example.liuhaoyuan.phonemanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liuhaoyuan.phonemanager.R;

/**
 * Created by liuhaoyuan on 2016/12/7.
 */

public class SettingItemView extends RelativeLayout {

    private TextView mTitle;
    private TextView mContent;
    private CheckBox mCheckBox;
    private String isChecked;
    private String title;
    private String onContent;
    private String offContent;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = View.inflate(context, R.layout.setting_item_view, this);
        mTitle = (TextView) view.findViewById(R.id.tv_setting_item_title);
        mContent = (TextView) view.findViewById(R.id.tv_setting_item_content);
        mCheckBox = (CheckBox) view.findViewById(R.id.cb_setting_item);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        isChecked = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "isChecked");
        offContent = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "off_content");
        onContent = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "on_content");

        mTitle.setText(title);
        if (isChecked()) {
            mContent.setText(onContent);
        } else{
            mContent.setText(offContent);
        }
    }

    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    public void setChecked(boolean check) {
        mCheckBox.setChecked(check);
        if (check){
            setContent(onContent);
        }else {
            setContent(offContent);
        }
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public String getContent() {
        return mContent.getText().toString();
    }

    public void setContent(String content) {
        mContent.setText(content);
    }
}
