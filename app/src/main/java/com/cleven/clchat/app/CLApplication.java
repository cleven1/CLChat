package com.cleven.clchat.app;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import dev.DevUtils;
import dev.utils.app.logger.DevLogger;
import dev.utils.app.logger.LogConfig;
import dev.utils.app.logger.LogLevel;
import okhttp3.OkHttpClient;

/**
 * Created by cleven on 2018/12/12.
 */

public class CLApplication extends Application {
    // 日志TAG
    private final String LOG_TAG = CLApplication.class.getSimpleName();

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;

        initDevUtil();

        initLQREmtion();

        initOkGo();

    }

    private void initOkGo() {
        //设置全局请求头,不支持中文,不允许有中文字符
        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");


        //设置全局请求参数,支持中文
//        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");
//        params.put("commonParamsKey2", "这里支持中文参数");

        //使用OkGo的拦截器
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("CLChat");
        //日志的打印范围
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //在logcat中的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        //默认是Debug日志类型
        builder.addInterceptor(loggingInterceptor);

        //设置请求超时时间,默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //连接超时时间
        //使用内存保存cookie,退出后失效
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        OkGo.getInstance()
                .init(this)
                .setOkHttpClient(builder.build())//不设置则使用默认
                .setCacheMode(CacheMode.NO_CACHE)//设置缓存模式
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)//设置缓存时间,默认永不过期
                .setRetryCount(3);//请求超时重连次数,默认3次
//                .addCommonHeaders(headers)
//                .addCommonParams(params);

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
