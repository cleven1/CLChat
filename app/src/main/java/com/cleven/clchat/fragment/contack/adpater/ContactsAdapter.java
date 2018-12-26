package com.cleven.clchat.fragment.contack.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleven.clchat.R;
import com.cleven.clchat.model.CLUserBean;
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

    private int headViewCount = 1;
    private List<CLUserBean> contacts;
    private static final String TAG = "ContactsAdapter";
    private final LayoutInflater inflater;

    public ContactsAdapter(Context context, List<CLUserBean> contacts) {
        this.contacts = contacts;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADVIEWTYPE){
            View headerView = inflater.inflate(R.layout.contact_headview_layuot,null);
            return new HeaderViewHolder(headerView);
        }else {
            View view = inflater.inflate(R.layout.layaout_item_contacts, null);
            return new ContactsViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            CLUserBean contact = contacts.get(position);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.setData(contact);
        }else {
            CLUserBean contact = contacts.get(getRealPostion(position));
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
        if (postion == headViewCount - 1){
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
        private CLUserBean data;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            unread_text = (TextView) itemView.findViewById(R.id.unread_count);

        }

        public void setData(CLUserBean data) {
            this.data = data;
            if (data.getUnreadNum() > 0){
                unread_text.setVisibility(View.VISIBLE);
            }else {
                unread_text.setVisibility(View.GONE);
            }
            unread_text.setText(CLUtils.formatUnreadNumber(data.getUnreadNum()));
        }
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        ImageView ivAvatar;
        TextView tvName;
        private CLUserBean data;

        ContactsViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        public void setData(CLUserBean contact,int position) {
            this.data = data;
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
