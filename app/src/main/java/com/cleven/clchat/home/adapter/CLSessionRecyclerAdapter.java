package com.cleven.clchat.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.home.Bean.CLSendStatus;
import com.cleven.clchat.manager.CLUserManager;

import java.util.List;

import dev.utils.app.SizeUtils;

import static com.cleven.clchat.home.Bean.CLMessageBodyType.MessageBodyType_Text;

/**
 * Created by cleven on 2018/12/14.
 */

public class CLSessionRecyclerAdapter extends RecyclerView.Adapter {

    /// 上下文
    private final Context mContext;
    private final List<CLMessageBean> mMessages;
    private final LayoutInflater layoutInflater;
    private int currentItemType;

    public CLSessionRecyclerAdapter(Context context, List<CLMessageBean> messages) {
        this.mContext = context;
        this.mMessages = messages;
        /// 初始化加载布局
        layoutInflater = LayoutInflater.from(this.mContext);
    }


    class CLMessageTextViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar;
        private TextView name;
        private ImageView sendfail;
        private ProgressBar pbBar;
        private final TextView mContent;
        private CLMessageBean data;

        public CLMessageTextViewHolder(Context mContext, View itemView, boolean isGroup) {
            super(itemView);
            LinearLayout contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayoout);
            ivAvatar = (ImageView)itemView.findViewById( R.id.iv_avatar );
            name = (TextView)itemView.findViewById( R.id.name );
            sendfail = (ImageView)itemView.findViewById( R.id.sendfail );
            pbBar = (ProgressBar)itemView.findViewById( R.id.pb_bar );
            mContent = (TextView) itemView.findViewById(R.id.content);

            //单聊改变布局
            if (isGroup == false){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentLayout.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.topMargin = SizeUtils.dipConvertPx(15);
                contentLayout.setLayoutParams(params);
                name.setVisibility(View.GONE);
            }else {
                name.setVisibility(View.VISIBLE);
            }

        }

        public void setData(final CLMessageBean data) {
            this.data = data;
            name.setText(data.getUserInfo().getName());
            mContent.setText(data.getContent());

//            Glide.with(mContext).load(data.getUserInfo().getAvatarUrl()).into(ivAvatar);
            // 发送失败
            if (CLSendStatus.fromTypeName(data.getSendStatus()) == CLSendStatus.SendStatus_FAILED){
                sendfail.setVisibility(View.VISIBLE);
                pbBar.setVisibility(View.GONE);
                sendfail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext,"重发",Toast.LENGTH_SHORT).show();
                    }
                });
            }else if (CLSendStatus.fromTypeName(data.getSendStatus()) == CLSendStatus.SendStatus_SEND){
                // 发送成功
                pbBar.setVisibility(View.GONE);
                sendfail.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        CLMessageBean messageBean = mMessages.get(i);
        if (CLMessageBodyType.fromTypeName(messageBean.getMessageType()) == MessageBodyType_Text){
            View baseView;
            /// 发送
            if (messageBean.getUserInfo().getUserId() == CLUserManager.getInstence().getUserInfo().getUserId()){
                baseView = layoutInflater.inflate(R.layout.message_right_text_item,null);
            }else { // 接受
                baseView = layoutInflater.inflate(R.layout.message_left_text_item,null);
            }
            return new CLMessageTextViewHolder(mContext, baseView,messageBean.isGroupSession());
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CLMessageTextViewHolder textViewHolder = (CLMessageTextViewHolder) holder;
        textViewHolder.setData(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}