package com.cleven.clchat.clchat.app;

import android.app.Application;

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

    @Override
    public void onCreate() {
        super.onCreate();

//        initDevUtil();
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
