package com.cleven.clchat.utils;

import android.content.Context;
import android.view.View;

import com.cleven.clchat.R;

import java.util.List;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.view.listener.OnBigImageClickListener;
import cc.shinichi.library.view.listener.OnBigImageLongClickListener;

public class CLPhotoBrowser {

    public static void Browser(Context context, List<String> imageUrlList,int currentIndex){
        ImagePreview
                .getInstance()
                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好
                .setContext(context)
                // 从第几张图片开始，索引从0开始哦~
                .setIndex(currentIndex)
                // 直接传url List
                .setImageList(imageUrlList)
                // 加载策略，详细说明见下面“加载策略介绍”。默认为手动模式
                .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysThumb)
                // 保存的文件夹名称，会在SD卡根目录进行文件夹的新建。
                // (你也可设置嵌套模式，比如："BigImageView/Download"，会在SD卡根目录新建BigImageView文件夹，并在BigImageView文件夹中新建Download文件夹)
                .setFolderName("CLChatPhoto")
                // 缩放动画时长，单位ms
                .setZoomTransitionDuration(300)
                // 是否启用点击图片关闭。默认启用
                .setEnableClickClose(true)
                // 是否启用上拉/下拉关闭。默认不启用
                .setEnableDragClose(true)
                // 是否显示关闭页面按钮，在页面左下角。默认不显示
                .setShowCloseButton(false)
                // 是否显示下载按钮，在页面右下角。默认显示
                .setShowDownButton(false)
                // 设置下载按钮图片资源，可不填，默认为：R.drawable.icon_download_new
                .setDownIconResId(R.drawable.icon_download_new)
                // 设置是否显示顶部的指示器（1/9）默认显示
                .setShowIndicator(true)
                // 设置失败时的占位图，默认为R.drawable.load_failed，设置为 0 时不显示
                .setErrorPlaceHolder(R.drawable.load_failed)
                // 点击回调
                .setBigImageClickListener(new OnBigImageClickListener() {
                    @Override public void onClick(View view, int position) {
                        // ...图片的点击事件
                    }
                })
                // 长按回调
                .setBigImageLongClickListener(new OnBigImageLongClickListener() {
                    @Override public void onLongClick(View view, int position) {
                        // ...图片的长按事件
                    }
                })
                // 开启预览
                .start();
    }

}
