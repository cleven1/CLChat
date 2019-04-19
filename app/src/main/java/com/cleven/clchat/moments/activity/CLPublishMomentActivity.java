package com.cleven.clchat.moments.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLFlutterViewUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.HashMap;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class CLPublishMomentActivity extends CLBaseActivity {

    private MethodChannel methodChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// 加载flutterView
        FlutterView flutterView = Flutter.createView(this, getLifecycle(), "publishPage");

        setContentView(R.layout.activity_clpublish_moment);

        setupTitleBar();

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_publish);

        /// 发送数据给flutterView
        new EventChannel(flutterView,"publishChannel").setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                HashMap params = new HashMap();
                params.put("user_id", CLUserManager.getInstence().getUserInfo().getUserId());
                eventSink.success(params);
            }
            @Override
            public void onCancel(Object o) {

            }
        });

        /// 创建methodChannel
        methodChannel = new MethodChannel(flutterView, "publishChannel");
        /// 监听flutter端事件
        methodChannel.setMethodCallHandler((call,result) -> {
            if (call.method.equals("publish_finish")){
                /// 发布完成回调状态
                setResult(CLAPPConst.PUBLISHMOMENTSFINISH);
                onBackPressed();
            }
        });
        frameLayout.addView(flutterView);
        frameLayout.setVisibility(View.INVISIBLE);

        /// 监听flutterView第一帧渲染
        CLFlutterViewUtil.firstFrameListener(frameLayout,flutterView);

    }

    private void setupTitleBar(){
        CommonTitleBar titleBar = (CommonTitleBar) findViewById(R.id.titlebar);
        TextView centerTextView = titleBar.getCenterTextView();
        centerTextView.setText("发布");
        TextView rightTextView = titleBar.getRightTextView();
        rightTextView.setVisibility(View.VISIBLE);
        rightTextView.setText("发布");
        rightTextView.setTextColor(Color.RED);
        /// 点击返回按钮
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    onBackPressed();
                }else if(action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                    Log.e("aa","发布");
                    methodChannel.invokeMethod("publish",null);
                }
            }
        });
    }
}
