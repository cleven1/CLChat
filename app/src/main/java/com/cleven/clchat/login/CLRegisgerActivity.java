package com.cleven.clchat.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.manager.CLUploadManager;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLHUDUtil;
import com.cleven.clchat.utils.CLImageLoadUtil;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.ui.ImagePreviewActivity;
import com.lqr.imagepicker.view.CropImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Map;

import dev.utils.LogPrintUtils;
import dev.utils.app.ADBUtils;
import dev.utils.common.validator.ValiToPhoneUtils;

public class CLRegisgerActivity extends CLBaseActivity implements View.OnClickListener {
    public static final int IMAGE_PICKER = 1;
    private EditText etMobile;
    private EditText etPassword;
    private TextView verifyTime;
    private Button btRegister;
    private EditText etVerifyCode;
    private ImageView mAvatar;

    /// 头像地址
    ImageItem imageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisger);
        findViews();
        initPickImage();
    }

    private void initPickImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new com.lqr.imagepicker.loader.ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                Glide.with(CLRegisgerActivity.this).load(Uri.parse("file://" + path).toString()).into(imageView);
            }
            @Override
            public void clearMemoryCache() {

            }
        });   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private void findViews() {
        mAvatar = findViewById(R.id.avatar);
        etMobile = (EditText)findViewById( R.id.et_mobile );
        etVerifyCode = (EditText)findViewById( R.id.et_verify_code );
        etPassword = (EditText)findViewById( R.id.et_password );
        verifyTime = (TextView)findViewById( R.id.verify_time );
        btRegister = (Button)findViewById( R.id.bt_register );

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CLRegisgerActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                imageItem = images.get(0);
                CLImageLoadUtil.loadRoundImg(mAvatar,imageItem.path,R.drawable.avatar,20);
            }
        }
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
                CLHUDUtil.showErrorHUD(CLRegisgerActivity.this,"验证码发送失败");
            }
        });

    }

    @Override
    public void onClick(View v) {
        if ( v == btRegister) {
            if (imageItem == null){
                Toast.makeText(this,"请选择头像",Toast.LENGTH_SHORT).show();
                return;
            }
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

            // 先上传头像
            CLHUDUtil.showLoading(this,"正在注册账号...");
            CLUploadManager.getInstance().uploadAvatar(imageItem.path, imageItem.name, new CLUploadManager.CLUploadOnLitenser() {
                @Override
                public void uploadSuccess(String fileName) {
                    registerHandler(fileName);
                }

                @Override
                public void uploadError(String fileName) {
                    CLHUDUtil.showErrorHUD(CLRegisgerActivity.this,"头像上传失败");
                }

                @Override
                public void uploadProgress(int progress) {

                }

                @Override
                public void uploadCancel(String fileName) {

                }
            });

        }
    }

    private void registerHandler(String avatarPath){
        final HttpParams params = new HttpParams();
        params.put("mobile",etMobile.getText().toString().trim());
        params.put("sms_code",etVerifyCode.getText().toString().trim());
        params.put("pwd",etPassword.getText().toString().trim());
        params.put("avatar",avatarPath);
        params.put("identifier",TextUtils.isEmpty(ADBUtils.getIMEI()) ? "" : ADBUtils.getIMEI());
        OkGo.<String>post(CLAPPConst.REGISTER).params(params).execute(new StringCallback(){
            @Override
            public void onSuccess(Response<String> response) {
                Map parse = (Map) JSON.parse(response.body());
                if (parse.get("error_code").equals("0")){
                    onBackPressed();
                }else {
                    Toast.makeText(CLRegisgerActivity.this,parse.get("error_msg").toString(),Toast.LENGTH_LONG).show();
                }
                CLHUDUtil.hideHUD();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                CLHUDUtil.hideHUD();
                Toast.makeText(CLRegisgerActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
