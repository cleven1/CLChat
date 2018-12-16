package com.cleven.clchat.app;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cleven.clchat.manager.CLMQTTManager;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;

import dev.DevUtils;
import dev.utils.app.logger.DevLogger;
import dev.utils.app.logger.LogConfig;
import dev.utils.app.logger.LogLevel;

/**
 * Created by cleven on 2018/12/12.
 */

public class CLApplication extends Application {
    // 日志TAG
    private final String LOG_TAG = CLApplication.class.getSimpleName();

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;

        initDevUtil();

        initLQREmtion();

        initMqtt();

    }

    /// 初始化表情功能
    private void initLQREmtion() {

        LQREmotionKit.init(this, new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
    }

    /// 连接MQTT
    private void initMqtt() {
        CLMQTTManager.getInstance().connect();
    }

    private void initDevUtil() {
        // 初始化工具类
        DevUtils.init(this.getApplicationContext());
        // == 初始化日志配置 ==
        // 设置默认Logger配置
        LogConfig logConfig = new LogConfig();
        logConfig.logLevel = LogLevel.DEBUG;
        logConfig.tag = LOG_TAG;
        DevLogger.init(logConfig);
        // 打开 lib 内部日志
        DevUtils.openLog();
        DevUtils.openDebug();
    }
}
