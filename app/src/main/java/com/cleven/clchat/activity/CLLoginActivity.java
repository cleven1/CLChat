package com.cleven.clchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;

import dev.utils.common.validator.ValiToPhoneUtils;

/**
 * Created by cleven on 2018/12/18.
 */

public class CLLoginActivity extends CLBaseActivity implements View.OnClickListener {

    private EditText etMobile;
    private EditText etPassword;
    private TextView verifyTime;
    private Button btLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        findViews();
    }

    private void findViews() {

        etMobile = (EditText)findViewById( R.id.et_mobile );
        etPassword = (EditText)findViewById( R.id.et_password );
        verifyTime = (TextView)findViewById( R.id.verify_time );
        btLogin = (Button)findViewById( R.id.bt_login );

        verifyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(0);
                verifyTime.setEnabled(false);
            }
        });

        btLogin.setOnClickListener( this );
    }
    private int timeCount = 60;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeCallbacksAndMessages(0);
            if (timeCount == 0){
                verifyTime.setText("获取验证码");
                verifyTime.setEnabled(true);
                timeCount = 60;
            }else {
                verifyTime.setText("" + timeCount);
                timeCount--;
                handler.sendEmptyMessageDelayed(0,1000);
            }
        }
    };


    @Override
    public void onClick(View v) {
        if ( v == btLogin ) {
            
            if (!ValiToPhoneUtils.isPhone(etMobile.getText().toString().trim())){
                Toast.makeText(this,"手机号不正确",Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this,MainActivity.class);

            startActivity(intent);

            finish();
        }
    }

}
