package com.cleven.clchat.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.R;
import com.cleven.clchat.activity.MainActivity;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.model.CLUserBean;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLHUDUtil;
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

    private EditText mEtMobile;
    private EditText mEtPassword;
    private Button mBtLogin;
    private Button mBtnRegister;
    private ImageButton mIb_qq;
    private ImageButton mIb_wx;
    private ImageButton mIb_wb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        findViews();
    }

    private void findViews() {

        mEtMobile = (EditText)findViewById( R.id.et_mobile );
        mEtPassword = (EditText)findViewById( R.id.et_password );
        mBtLogin = (Button)findViewById( R.id.bt_login );
        mBtnRegister = findViewById(R.id.bt_register);
        mIb_qq = (ImageButton)findViewById(R.id.ib_qq);
        mIb_wx = findViewById(R.id.ib_wx);
        mIb_wb = findViewById(R.id.ib_wb);

        mBtLogin.setOnClickListener( this );
        mBtnRegister.setOnClickListener( this );
        mIb_qq.setOnClickListener(this);
        mIb_wx.setOnClickListener(this);
        mIb_wb.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if ( v == mBtLogin) {
            
            if (!ValiToPhoneUtils.isPhone(mEtMobile.getText().toString().trim())){
                Toast.makeText(this,"手机号不正确",Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mEtPassword.getText())) {
                Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            HttpParams params = new HttpParams();
            params.put("mobile", mEtMobile.getText().toString().trim());
            params.put("pwd", mEtPassword.getText().toString().trim());
            params.put("identifier","Android");
            CLHUDUtil.showLoading(this);
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
                            CLHUDUtil.hideHUD();
                        }else {
                            LogPrintUtils.eTag("LOGIN","转模型失败");
                            CLHUDUtil.showErrorHUD(CLLoginActivity.this,parseMap.get("error_msg").toString());
                        }
                    }else {
                        CLHUDUtil.showErrorHUD(CLLoginActivity.this,parseMap.get("error_msg").toString());
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    CLHUDUtil.showErrorHUD(CLLoginActivity.this,"登录失败");
                }
            });

        }else if (v == mBtnRegister){
            Intent intent = new Intent(this,CLRegisgerActivity.class);

            startActivity(intent);
        }else if (v == mIb_qq){
            CLHUDUtil.showSuccessHUD(this,"QQ");
        }else if (v == mIb_wx){
            CLHUDUtil.showErrorHUD(this,"微信");
        }else if (v == mIb_wb){
            CLHUDUtil.showTextHUD(this,"微博");
        }
    }

}
