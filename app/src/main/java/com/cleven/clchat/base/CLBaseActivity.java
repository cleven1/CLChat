package com.cleven.clchat.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.cleven.clchat.R;

public class CLBaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /// 取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clbase);

    }


}
