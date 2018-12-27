package com.cleven.clchat.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.ImageView;

import com.cleven.clchat.R;
import com.kaopiz.kprogresshud.KProgressHUD;

public class CLHUDUtil {

    private static KProgressHUD hud;

    /**
     * 文字提示
     * @param context
     * @param text
     */
    public static void showTextHUD(Context context,String text){
        hideHUD();
        hud = KProgressHUD.create(context)
                .setCustomView(new ImageView(context))
                .setLabel(text)
                .show();
        scheduleDismiss();
    }

    /**
     * 成功提示
     * @param context
     * @param text
     */
    public static void showSuccessHUD(Context context,String text){
        hideHUD();
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.hud_success);
//        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
//        drawable.start();
        hud = KProgressHUD.create(context)
                .setCustomView(imageView)
                .setLabel(text)
                .show();
        scheduleDismiss();
    }

    /**
     * 失败提示
     * @param context
     * @param text
     */
    public static void showErrorHUD(Context context,String text){
        hideHUD();
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.hud_error);
//        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
//        drawable.start();
        hud = KProgressHUD.create(context)
                .setCustomView(imageView)
                .setLabel(text)
                .show();
        scheduleDismiss();
    }

    /**
     * 不带文字的loading
     * @param context
     */
    public static void showLoading(Context context){
        showLoading(context,null);
    }

    /**
     * 带文字的loading
     * @param context
     * @param text
     */
    public static void showLoading(Context context, String text){
        hideHUD();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .show();
    }

    /**
     * 带cancel回调的loading
     * @param onCancelListener
     */
    public static void showCancelLoading(final Context context, DialogInterface.OnCancelListener onCancelListener){
        showCancelLoading(context,null,onCancelListener);
    }

    /**
     * 带cancel回调的loading
     * @param context
     * @param text
     * @param onCancelListener
     */
    public static void showCancelLoading(final Context context, String text, DialogInterface.OnCancelListener onCancelListener){
        hideHUD();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(text)
                .setCancellable(onCancelListener)
                .show();
    }

    /**
     * 带下载进度的
     * @param context
     * @param text
     * @param progress
     */
    public static void showDownloading(Context context,String text,int progress){
        hideHUD();
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                .setLabel(text)
                .show();
        hud.setMaxProgress(100);
        hud.setProgress(progress);
    }


    public static void hideHUD(){
        if (hud != null){
            hud.dismiss();
        }
    }

    private static void scheduleDismiss() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
            }
        }, 1500);
    }

}
