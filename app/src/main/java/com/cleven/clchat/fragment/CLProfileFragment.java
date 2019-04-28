package com.cleven.clchat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleven.clchat.API.OkGoUtil;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.login.CLLoginActivity;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.utils.CLHUDUtil;
import com.cleven.clchat.utils.CLImageLoadUtil;

import java.util.Map;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLProfileFragment extends CLBaseFragment {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mUserId;
    private Button mLogout;

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.profile_layout,null);
        findViews(view);
        return view;
    }

    private void findViews(View view){
        mAvatar = view.findViewById(R.id.iv_avatar);
        mName = view.findViewById(R.id.tv_name);
        mUserId = view.findViewById(R.id.tv_userId);
        mLogout = view.findViewById(R.id.btn_logout);
    }

    @Override
    public void initData() {
        super.initData();

        CLImageLoadUtil.loadRoundImg(mAvatar,CLUserManager.getInstence().getUserInfo().getAvatarUrl(),R.drawable.avatar,20);
        mName.setText(CLUserManager.getInstence().getUserInfo().getName());
        mUserId.setText("ID: " + CLUserManager.getInstence().getUserInfo().getUserId());
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGoUtil.postRequest("user/logoff", "logout", null, new OkGoUtil.CLNetworkCallBack() {
                    @Override
                    public void onSuccess(Map response) {
                        Intent intent = new Intent(mContext,CLLoginActivity.class);

                        startActivity(intent);

                        CLUserManager.getInstence().setUserInfo(null);

                        Activity activity = (Activity) CLProfileFragment.this.mContext;
                        activity.finish();
                        activity.overridePendingTransition(R.anim.cl_fade_in,R.anim.cl_fade_out);

                    }

                    @Override
                    public void onFailure(Map error) {
                        CLHUDUtil.showErrorHUD(mContext,"退出失败");
                    }
                });
            }
        });
    }
}
