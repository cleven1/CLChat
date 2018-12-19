package com.cleven.clchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.model.CLUserBean;
import com.cleven.clchat.utils.CLAPPConst;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.Map;

import dev.utils.LogPrintUtils;
import dev.utils.common.validator.ValiToPhoneUtils;

/**
 * Created by cleven on 2018/12/18.
 */

public class CLLoginActivity extends CLBaseActivity implements View.OnClickListener {

    private EditText etMobile;
    private EditText etPassword;
    private Button btLogin;
    private Button btnRegister;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        findViews();
    }

    private void findViews() {

        etMobile = (EditText)findViewById( R.id.et_mobile );
        etPassword = (EditText)findViewById( R.id.et_password );
        btLogin = (Button)findViewById( R.id.bt_login );
        btnRegister = findViewById(R.id.bt_register);

        btLogin.setOnClickListener( this );
        btnRegister.setOnClickListener( this );
    }


    @Override
    public void onClick(View v) {
        if ( v == btLogin ) {
            
            if (!ValiToPhoneUtils.isPhone(etMobile.getText().toString().trim())){
                Toast.makeText(this,"手机号不正确",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(etPassword.getText())) {
                Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            HttpParams params = new HttpParams();
            params.put("mobile",etMobile.getText().toString().trim());
            params.put("pwd",etPassword.getText().toString().trim());
            params.put("identifier","Android");
            OkGo.<String>post(CLAPPConst.LOGIN).params(params).execute(new StringCallback(){
                @Override
                public void onSuccess(Response<String> response) {
                    Map parseMap = (Map)JSON.parse(response.body());
                    if (parseMap.get("error_code").equals("0")){ //登录成功
                        CLUserBean userBean = JSON.parseObject(parseMap.get("data").toString(), CLUserBean.class);
                        if (userBean.getUserId() != null){
                            CLUserManager.getInstence().setUserInfo(userBean);

                            Intent intent = new Intent(CLLoginActivity.this,MainActivity.class);

                            startActivity(intent);

                            finish();
                        }else {
                            LogPrintUtils.eTag("LOGIN","转模型失败");
                        }
                    }else {
                        Toast.makeText(CLLoginActivity.this,parseMap.get("error_msg").toString(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    Toast.makeText(CLLoginActivity.this,"登录失败",Toast.LENGTH_LONG).show();
                }
            });

        }else if (v == btnRegister){
            Intent intent = new Intent(this,CLRegisgerActivity.class);

            startActivity(intent);
        }
    }

}
