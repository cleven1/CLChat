package com.cleven.clchat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.moments.activity.CLMomentsActivity;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLMomentsFragment extends CLBaseFragment {
    @Override
    public View initView() {

        LinearLayout view = (LinearLayout)View.inflate(mContext, R.layout.ll_moments, null);

        FlutterView flutterView = Flutter.createView((Activity) mContext, getLifecycle(), "moments");

        MethodChannel channel = new MethodChannel(flutterView,"moment");

        /// 监听点击flutter cell
        channel.setMethodCallHandler((methodCall ,result) -> {
            if (methodCall.method.equals("gotoDetailPage")) { // 取值
                String arguments = (String) methodCall.arguments;
                Intent intent = new Intent(mContext,CLMomentsActivity.class);
                intent.putExtra("moment_id", arguments);
                mContext.startActivity(intent);
            }
        });

        view.addView(flutterView);

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        System.out.println("加载数据");
    }
}
