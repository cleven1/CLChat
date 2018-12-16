package com.cleven.clchat.clchat.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cleven.clchat.clchat.R;

public class CLBaseActivity extends FragmentActivity {

    private LinearLayout mNavgationBar;
    private ImageView mLeft_bar;
    private TextView mTitle;
    private ImageView mRight_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /// 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clbase);

//        mNavgationBar = (LinearLayout) findViewById(R.id.navgationBar);
//        mLeft_bar = (ImageView) findViewById(R.id.left_bar);
//        mTitle = (TextView) findViewById(R.id.tv_title);
//        mRight_bar = (ImageView) findViewById(R.id.right_bar);
//
//        mLeft_bar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setLeftBarListener(view);
//            }
//        });
//
//        mRight_bar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setRightBarListener(view);
//            }
//        });
    }

    /**
     * 设置隐藏或者显示导航栏
     * @param isVisible
     */
    public void setNavgationBarVisible(boolean isVisible) {
        mNavgationBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    /**
     * 设置显示或者隐藏leftBar
     * @param isVisible
     */
    public void setLeftBarVisible(boolean isVisible){
        mLeft_bar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置显示或者隐藏RightBar
     * @param isVisible
     */
    public void setRightBarVisible(boolean isVisible){
        mRight_bar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置rightBar图片
     * @param resource
     */
    public void setRightBarImage(int resource){
        mRight_bar.setBackgroundResource(resource);
    }

    /**
     * 设置leftBar监听事件
     * @param view
     */
    public void setLeftBarListener(View view){

    }

    /**
     * 设置rightBar监听事件
     * @param view
     */
    public void setRightBarListener(View view){

    }

}
