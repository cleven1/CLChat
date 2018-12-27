package com.cleven.clchat.fragment.contack.adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.contack.activity.CLAddFriendActivity;
import com.cleven.clchat.contack.bean.CLFriendBean;
import com.cleven.clchat.utils.CLImageLoadUtil;
import com.cleven.clchat.utils.CLUtils;

import java.util.List;

/**
 * Contact联系人适配器
 *
 * @author nanchen
 * @fileName WaveSideBarView
 * @packageName com.nanchen.wavesidebarview
 * @date 2016/12/27  15:33
 * @github https://github.com/nanchen2251
 */

public class ContactsAdapter extends RecyclerView.Adapter{

    private static final int HEADVIEWTYPE = 10000;
    private static final int NORMAL = 11111;
    private final Context mContext;

    private int headViewCount = 2;
    private List<CLFriendBean> contacts;
    private static final String TAG = "ContactsAdapter";
    private final LayoutInflater inflater;

    public ContactsAdapter(Context context, List<CLFriendBean> contacts) {
        this.contacts = contacts;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADVIEWTYPE){
            View headerView = inflater.inflate(R.layout.custom_cell_layuot,null);
            return new HeaderViewHolder(headerView);
        }else {
            View view = inflater.inflate(R.layout.layaout_item_contacts, null);
            return new ContactsViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            CLFriendBean contact = contacts.get(position);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.setData(contact,position);
        }else {
            CLFriendBean contact = contacts.get(getRealPostion(position));
            ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            Log.e(TAG, "onBindViewHolder: index:" + contact.getIndex());
            contactsViewHolder.setData(contact,getRealPostion(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return getRealViewType(position);
    }

    private int getRealViewType(int postion){
        if (postion < headViewCount){
            return HEADVIEWTYPE;
        }else {
            return NORMAL;
        }
    }
    private int getRealPostion(int postion){
        return postion;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView unread_text;
        private final ImageView iv_avatar;
        private final TextView name;
        private CLFriendBean data;
        private final LinearLayout contentView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            contentView = (LinearLayout) itemView.findViewById(R.id.contentView);
            unread_text = (TextView) itemView.findViewById(R.id.unread_count);
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            name = (TextView) itemView.findViewById(R.id.name);

        }

        public void setData(CLFriendBean data, final int postion) {
            this.data = data;
            if (data.getUnreadNum() > 0){
                unread_text.setVisibility(View.VISIBLE);
            }else {
                unread_text.setVisibility(View.GONE);
            }
            unread_text.setText(CLUtils.formatUnreadNumber(data.getUnreadNum()));
            name.setText(data.getName());
            if (postion == 0){
                iv_avatar.setImageResource(R.drawable.cl_message_noti);
            }else {
                iv_avatar.setImageResource(R.mipmap.ic_around_blue);
            }

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"postion = "+postion,Toast.LENGTH_SHORT).show();
                    if (postion == 1){ // 好友通知
                        Intent intent = new Intent(mContext,CLAddFriendActivity.class);
                        intent.putExtra("title","好友通知");
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        ImageView ivAvatar;
        TextView tvName;
        private CLFriendBean data;

        ContactsViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        public void setData(CLFriendBean contact,int position) {
            this.data = contact;
            if (position == 0 || !contacts.get(position - 1).getIndex().equals(contact.getIndex())) {
                tvIndex.setVisibility(View.VISIBLE);
                tvIndex.setText(contact.getIndex());
            } else {
                tvIndex.setVisibility(View.GONE);
            }
            tvName.setText(contact.getName());
            CLImageLoadUtil.loadRoundImg(ivAvatar,contact.getAvatarUrl(),R.drawable.avatar,20);
        }
    }
}
