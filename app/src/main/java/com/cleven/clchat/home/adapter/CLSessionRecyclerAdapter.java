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

import com.cleven.clchat.R;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;

import java.util.List;

import static com.cleven.clchat.home.Bean.CLMessageBodyType.MessageBodyType_Text;
import static com.cleven.clchat.home.Bean.CLMessageDirection.MessageDirection_SEND;

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
                contentLayout.setLayoutParams(params);
            }

        }

        public void setData(CLMessageBean data) {
            this.data = data;
//            name.setText("");
            mContent.setText(data.getContent());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        CLMessageBean messageBean = mMessages.get(i);
        if (CLMessageBodyType.fromTypeName(messageBean.getMessageType()) == MessageBodyType_Text){
            View baseView;
            /// 发送
            if (messageBean.getMessageDirection() == MessageDirection_SEND){
                baseView = layoutInflater.inflate(R.layout.message_right_text_item,null);
            }else { // 接受
                baseView = layoutInflater.inflate(R.layout.message_left_text_item,null);
            }
            return new CLMessageTextViewHolder(mContext, baseView,true);
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
