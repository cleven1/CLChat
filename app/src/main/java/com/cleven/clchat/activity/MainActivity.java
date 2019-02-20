package com.cleven.clchat.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.fragment.CLDiscoverFragment;
import com.cleven.clchat.fragment.CLProfileFragment;
import com.cleven.clchat.fragment.contack.CLContactFragment;
import com.cleven.clchat.fragment.home.CLHomeFragment;
import com.cleven.clchat.manager.CLMQTTManager;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CLBaseActivity {

    private FrameLayout frameLayout;
    private RadioGroup radioGroup;

    private int currentSelectIndex = 0;
    /// 记录当前显示的fragment
    private CLBaseFragment preFragment;
    private List<CLBaseFragment> fragments;
    private TextView centerTextView;
    private CommonTitleBar titleBar;
    private TextView rightTextView;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /// 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupTitleBar();

        initFragments();

        findViews();

        initMqtt();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

       if (CLMQTTManager.getInstance().getCurrentStatus() != CLMQTTManager.CLMQTTStatus.connect_succss) {
            CLMQTTManager.getInstance().connectMQTT(this);
        }
    }
    /**
     * 原理  去除Super 切断原有恢复逻辑 保存位置
     * @param outState
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* 记录当前的position */
        outState.putInt("position", mPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPosition = savedInstanceState.getInt("position");
        /// 获取Fragment
        CLBaseFragment fragment = getFragment(mPosition);
        /// 切换
        switchFragment(preFragment,fragment);
    }


    /// 连接MQTT
    private void initMqtt() {
        CLMQTTManager.getInstance().connectMQTT(this);
        CLMQTTManager.getInstance().setConnectStatusOnListener(new CLMQTTManager.CLMQTTConnectStatusOnListener() {
            @Override
            public void onConnectStatus(CLMQTTManager.CLMQTTStatus status) {
                if (currentSelectIndex != 0){
                    return;
                }
                if (status == CLMQTTManager.CLMQTTStatus.connect_succss) {
                    centerTextView.setText("首页");
                }else if (status == CLMQTTManager.CLMQTTStatus.connect_fail){
                    centerTextView.setText("连接失败");
                }else {
                    centerTextView.setText("连接中...");
                }
            }
        });
    }
    
    private void findViews() {

        frameLayout = (FrameLayout)findViewById( R.id.frameLayout );
        radioGroup = (RadioGroup)findViewById( R.id.radioGroup );
        /// 设置监听事件
        radioGroup.setOnCheckedChangeListener(new MyOnClickListener());
        /// 默认选中第一个
        radioGroup.check(R.id.rb_main_home);
    }

    private void setupTitleBar(){
        titleBar = (CommonTitleBar) findViewById(R.id.titlebar);
        ImageButton leftImageButton = titleBar.getLeftImageButton();
        leftImageButton.setVisibility(View.GONE);
        rightTextView = titleBar.getRightTextView();
        rightTextView.setVisibility(View.GONE);
        centerTextView = titleBar.getCenterTextView();
        centerTextView.setText("首页");
    }

    private void setRightBarShowOrHidden(Boolean isShow){
        rightTextView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private class MyOnClickListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_main_home:
                    currentSelectIndex = 0;
                    centerTextView.setText("首页");
                    setRightBarShowOrHidden(false);
                    break;
                case R.id.rb_main_contact:
                    currentSelectIndex = 1;
                    centerTextView.setText("联系人");
                    setRightBarShowOrHidden(true);
                    break;
                case R.id.rb_main_discover:
                    currentSelectIndex = 2;
                    centerTextView.setText("发现");
                    setRightBarShowOrHidden(false);
                    break;
                case R.id.rb_main_profile:
                    currentSelectIndex = 3;
                    centerTextView.setText("我");
                    setRightBarShowOrHidden(false);
                    break;
            }
            /// 获取Fragment
            CLBaseFragment fragment = getFragment(currentSelectIndex);
            /// 切换
            switchFragment(preFragment,fragment);
        }

    }

    /// 根据下标获取fragment
    private CLBaseFragment getFragment(int index){
        if (fragments != null && fragments.size() > 0){
            return fragments.get(index);
        }
        return null;
    }

    /// 切换Fragment
    private void switchFragment(Fragment fromFragment, CLBaseFragment nextFragment){
        if (preFragment != nextFragment){
            preFragment = nextFragment;
            if (nextFragment != null){
                /// 获取Fragment管理器
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                // 判断nextFragment是否添加
                if (!nextFragment.isAdded()){
                    //隐藏当前Fragment
                    if (fromFragment != null){
                        transaction.hide(fromFragment);
                    }
                    // 添加下一个Fragment
                    transaction.add(R.id.frameLayout,nextFragment).commit();
                }else {
                    //隐藏当前Fragment
                    if (fromFragment != null){
                        transaction.hide(fromFragment);
                    }
                    /// 如果已经添加过,直接显示
                    transaction.show(nextFragment).commit();
                }
            }
        }
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new CLHomeFragment());
        CLContactFragment contactFragment = new CLContactFragment();
        contactFragment.setTitleBar(titleBar);
        fragments.add(contactFragment);
        fragments.add(new CLDiscoverFragment());
        fragments.add(new CLProfileFragment());
    }


}
