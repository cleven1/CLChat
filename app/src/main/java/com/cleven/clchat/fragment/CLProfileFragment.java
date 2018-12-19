package com.cleven.clchat.fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.utils.CLAPPConst;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import dev.utils.LogPrintUtils;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLProfileFragment extends CLBaseFragment {
    @Override
    public View initView() {

        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setText("我");
        textView.setTextSize(40);
        textView.setTextColor(Color.BLACK);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                HttpParams params = new HttpParams();
//                params.put("user_id","693751");
                OkGo.<String>get(CLAPPConst.HTTP_SERVER_BASE_URL + "friend/queryFriend?user_id=693751").execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogPrintUtils.eTag("查询",response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        LogPrintUtils.eTag("查询失败",response.body());
                    }
                });
            }
        });

        return textView;
    }

    @Override
    public void initData() {
        super.initData();

        System.out.println("加载数据");
    }
}
