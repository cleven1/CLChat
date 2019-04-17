package com.cleven.clchat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.moments.activity.CLMomentsActivity;
import com.cleven.clchat.moments.activity.CLPublishMomentActivity;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLPhotoBrowser;

import java.util.List;
import java.util.Map;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLMomentsFragment extends CLBaseFragment {

    private MethodChannel channel;

    @Override
    public View initView() {

        LinearLayout view = (LinearLayout)View.inflate(mContext, R.layout.ll_moments, null);

        FlutterView flutterView = Flutter.createView((Activity) mContext, getLifecycle(), "moments");

        channel = new MethodChannel(flutterView,"moment");

        /// 监听点击flutter cell
        channel.setMethodCallHandler((methodCall , result) -> {
            if (methodCall.method.equals("gotoDetailPage")) { // 取值
                String arguments = (String) methodCall.arguments;
                Intent intent = new Intent(mContext,CLMomentsActivity.class);
                intent.putExtra("moment_id", arguments);
                mContext.startActivity(intent);
            }else if (methodCall.method.equals("gotoMomentPublish")){
                Intent intent = new Intent(mContext, CLPublishMomentActivity.class);
                startActivityForResult(intent,CLAPPConst.PUBLISHMOMENTSFINISH);
            }else if (methodCall.method.equals("photoBrowser")) {
                Map params = (Map) methodCall.arguments;
                Log.e("photo",params.toString());
                List pics = (List) params.get("pics");
                int index = (int)params.get("index");
                CLPhotoBrowser.Browser(mContext,pics,index);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLAPPConst.PUBLISHMOMENTSFINISH){ // 发布完成
            channel.invokeMethod("updateMomentsData","");
        }
    }
}
