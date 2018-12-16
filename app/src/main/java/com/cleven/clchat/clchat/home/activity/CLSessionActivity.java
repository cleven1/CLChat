package com.cleven.clchat.clchat.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cleven.clchat.clchat.R;
import com.cleven.clchat.clchat.base.CLBaseActivity;
import com.cleven.clchat.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.clchat.home.Bean.CLMessageDirection;
import com.cleven.clchat.clchat.home.adapter.CLSessionRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CLSessionActivity extends CLBaseActivity implements View.OnClickListener {


    private RecyclerView rvSessionView;
    private ImageButton ibVoice;
    private EditText etText;
    private ImageButton ibEmtion;
    private ImageButton ibMore;
    private List<CLMessageBean> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        findViews();
    }

    private void findViews() {
        rvSessionView = (RecyclerView)findViewById( R.id.rv_sessionView );
        ibVoice = (ImageButton)findViewById( R.id.ib_voice );
        etText = (EditText)findViewById( R.id.et_text );
        ibEmtion = (ImageButton)findViewById( R.id.ib_emtion );
        ibMore = (ImageButton)findViewById( R.id.ib_more );

        ibVoice.setOnClickListener( this );
        ibEmtion.setOnClickListener( this );
        ibMore.setOnClickListener( this );

        messageList = new ArrayList<>();
        CLMessageBean message = new CLMessageBean();
        message.setContent("消息上噶手机关机按实际噶开始上课了几个卡手机观看解");
//        message.setUserInfo(new CLUserBean().setName("张三"));
        message.setMessageType(CLMessageBodyType.MessageBodyType_Text);
        message.setMessageDirection(CLMessageDirection.MessageDirection_SEND);
        messageList.add(message);

        CLMessageBean message1 = new CLMessageBean();
        message1.setContent("ajwqjkg是看了几个了可靠健康管理科技管理为控股而更为快捷管理科文件柜q");
//        message.setUserInfo(new CLUserBean().setName("张三"));
        message1.setMessageType(CLMessageBodyType.MessageBodyType_Text);
        message1.setMessageDirection(CLMessageDirection.MessageDirection_RECEIVE);
        messageList.add(message1);
        // 设置适配器
        rvSessionView.setAdapter(new CLSessionRecyclerAdapter(this,messageList));
        rvSessionView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

    }


    @Override
    public void onClick(View v) {
        if ( v == ibVoice ) {
            // Handle clicks for ibVoice
        } else if ( v == ibEmtion ) {
            // Handle clicks for ibEmtion
        } else if ( v == ibMore ) {
            // Handle clicks for ibMore
        }
    }


}
