package com.cleven.clchat.utils;

import android.view.View;

import io.flutter.view.FlutterView;
import me.jessyan.autosize.utils.LogUtils;

public class CLFlutterViewUtil {

    /// 监听第一帧加载
    public static void firstFrameListener(View view, FlutterView flutterView){

        /// 监听flutterView第一帧渲染
        FlutterView.FirstFrameListener[] listeners = new FlutterView.FirstFrameListener[1];
        listeners[0] = new FlutterView.FirstFrameListener() {
            @Override
            public void onFirstFrame() {
                LogUtils.e("加载完成");
                view.setVisibility(View.VISIBLE);
            }
        };
        flutterView.addFirstFrameListener(listeners[0]);
    }
}
