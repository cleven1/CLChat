package com.cleven.clchat.moments.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.manager.CLUserManager;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.HashMap;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.view.FlutterView;

public class CLMomentsActivity extends CLBaseActivity {

    private CommonTitleBar titleBar;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clmoments);

        /// 获取参数
        String moment_id = getIntent().getStringExtra("moment_id");

        Log.i("moment_id",moment_id);
        setupTitleBar();

        frameLayout = (FrameLayout) findViewById(R.id.fl_layout);

        FlutterView flutterView = Flutter.createView(this, getLifecycle(), "detailPage");

        /// 发送数据给flutterView
        new EventChannel(flutterView,"detailChannel").setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                HashMap params = new HashMap();
                params.put("moment_id",moment_id);
                params.put("user_id",CLUserManager.getInstence().getUserInfo().getUserId());
                eventSink.success(params);
            }
            @Override
            public void onCancel(Object o) {

            }
        });
        frameLayout.addView(flutterView);
    }


    private void setupTitleBar(){
        titleBar = (CommonTitleBar) findViewById(R.id.titlebar);
        TextView centerTextView = titleBar.getCenterTextView();
        centerTextView.setText("详情");
        TextView rightTextView = titleBar.getRightTextView();
        rightTextView.setVisibility(View.GONE);
        /// 点击返回按钮
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    onBackPressed();
                }
            }
        });
    }
}
