package com.cleven.clchat.clchat.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.cleven.clchat.clchat.base.CLBaseFragment;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLDiscoverFragment extends CLBaseFragment {
    @Override
    public View initView() {

        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setText("发现");
        textView.setTextSize(40);
        textView.setTextColor(Color.BLACK);

        return textView;
    }

    @Override
    public void initData() {
        super.initData();

        System.out.println("加载数据");
    }
}
