package com.cleven.clchat.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.utils.CLAPPConst;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.Map;

import dev.utils.LogPrintUtils;
import dev.utils.common.validator.ValiToPhoneUtils;

public class CLRegisgerActivity extends CLBaseActivity implements View.OnClickListener {

    private EditText etMobile;
    private EditText etPassword;
    private TextView verifyTime;
    private Button btRegister;
    private EditText etVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisger);
        findViews();
    }
    private void findViews() {

        etMobile = (EditText)findViewById( R.id.et_mobile );
        etVerifyCode = (EditText)findViewById( R.id.et_verify_code );
        etPassword = (EditText)findViewById( R.id.et_password );
        verifyTime = (TextView)findViewById( R.id.verify_time );
        btRegister = (Button)findViewById( R.id.bt_register );

        verifyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(0);
                verifyTime.setEnabled(false);
                getSmsCode();
            }
        });

        btRegister.setOnClickListener( this );
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


    private void getSmsCode(){
        if (!ValiToPhoneUtils.isPhone(etMobile.getText().toString().trim())){
            Toast.makeText(this,"手机号不正确",Toast.LENGTH_SHORT).show();
            return;
        }

        HttpParams params = new HttpParams();
        params.put("mobile",etMobile.getText().toString().trim());
        OkGo.<String>post(CLAPPConst.SMSCODE).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                LogPrintUtils.eTag("SMS",response.body());
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                LogPrintUtils.eTag("SMS",response.body());
            }
        });

    }

    @Override
    public void onClick(View v) {
        if ( v == btRegister) {

            if (!ValiToPhoneUtils.isPhone(etMobile.getText().toString().trim())){
                Toast.makeText(this,"手机号不正确",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etVerifyCode.getText())) {
                Toast.makeText(this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (etVerifyCode.getText().toString().trim().length() != 4) {
                Toast.makeText(this,"验证码长度不正确",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etPassword.getText())) {
                Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            final HttpParams params = new HttpParams();
            params.put("mobile",etMobile.getText().toString().trim());
            params.put("sms_code",etVerifyCode.getText().toString().trim());
            params.put("pwd",etPassword.getText().toString().trim());
            params.put("identifier","Android");
            OkGo.<String>post(CLAPPConst.REGISTER).params(params).execute(new StringCallback(){
                @Override
                public void onSuccess(Response<String> response) {
                    Map parse = (Map) JSON.parse(response.body());
                    if (parse.get("error_code").equals("0")){
                        onBackPressed();
                    }else {
                        Toast.makeText(CLRegisgerActivity.this,parse.get("error_msg").toString(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    Toast.makeText(CLRegisgerActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}
