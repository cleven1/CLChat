package com.cleven.clchat.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.home.CLEmojiCommon.utils.CLEmojiFileUtils;

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
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, CLEmojiFileUtils.getFolderPath("assets"),Toast.LENGTH_SHORT).show();
            }
        });

        return textView;
    }

    @Override
    public void initData() {
        super.initData();

        System.out.println("加载数据");
    }
}
