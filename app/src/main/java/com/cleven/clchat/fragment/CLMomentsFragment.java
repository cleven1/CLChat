package com.cleven.clchat.fragment;

import android.app.Activity;
import android.view.View;

import com.cleven.clchat.base.CLBaseFragment;

import io.flutter.facade.Flutter;
import io.flutter.view.FlutterView;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLMomentsFragment extends CLBaseFragment {
    @Override
    public View initView() {
        FlutterView flutterView = Flutter.createView((Activity) mContext, getLifecycle(), "moments");
//        flutterView.setBackgroundColor(Color.WHITE);
        return flutterView;
    }

    @Override
    public void initData() {
        super.initData();

        System.out.println("加载数据");
    }
}
