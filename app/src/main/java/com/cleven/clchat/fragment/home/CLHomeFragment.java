package com.cleven.clchat.fragment.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.fragment.home.bean.CLSessionBean;
import com.cleven.clchat.home.CLEmojiCommon.utils.CLEmojiCommonUtils;
import com.cleven.clchat.home.activity.CLSessionActivity;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLImageLoadUtil;

import java.util.ArrayList;
import java.util.List;

import dev.utils.LogPrintUtils;

import static dev.DevUtils.runOnUiThread;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLHomeFragment extends CLBaseFragment {

    private ListView listView;
    private List<CLSessionBean> dataArray;
    private MyAdapter adapter;

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.session_listview,null);
        listView = (ListView) view.findViewById(R.id.listView);

        dataArray = new ArrayList<>();

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        /// 查询消息会话
        dataArray = CLSessionBean.getAllSessionData();

        //设置适配器
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new MyOnItemClickListener());

        /// 监听数据插入数据库成功后回调
        CLSessionBean.setReceiveSessionOnListener(new CLSessionBean.CLReceiveSessionOnListener() {
            @Override
            public void onMessage() {
                dataArray = CLSessionBean.getAllSessionData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CLSessionBean sessionBean = dataArray.get(i);
            Intent intent = new Intent(mContext,CLSessionActivity.class);
            intent.putExtra("userName", sessionBean.getName());
            String userId = sessionBean.getCurrentUserId().equals(CLUserManager.getInstence().getUserInfo().getUserId()) ? sessionBean.getTargetId() : sessionBean.getCurrentUserId();
            intent.putExtra("userId", userId);
            LogPrintUtils.eTag("aaa",userId);
            startActivityForResult(intent,CLAPPConst.SESSIONMESSAGERESULTCODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CLAPPConst.SESSIONMESSAGERESULTCODE){
            /// 查询消息会话
            dataArray = CLSessionBean.getAllSessionData();
            adapter.notifyDataSetChanged();
        }
    }

    /// 适配器
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataArray.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, android.view.View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null){
                viewHolder = new ViewHolder();
                view = View.inflate(mContext,R.layout.session_item,null);
                viewHolder.avatar = view.findViewById(R.id.iv_avatar1);
                viewHolder.nickName = view.findViewById(R.id.tv_title);
                viewHolder.content = view.findViewById(R.id.tv_content);
                viewHolder.time = view.findViewById(R.id.tv_time);
                viewHolder.unread_count = view.findViewById(R.id.unread_count);
                viewHolder.group_avatar_layout = view.findViewById(R.id.sessionList_groupAvatar);
                viewHolder.singnle_avatar_layout = view.findViewById(R.id.sessionList_singleAvatar);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }

            CLSessionBean sessionBean = dataArray.get(i);
            if (TextUtils.isEmpty(sessionBean.getContent())){
                viewHolder.content.setVisibility(View.GONE);
            }else {
                viewHolder.content.setVisibility(View.VISIBLE);
                /// 过滤出表情,直接展示
                CLEmojiCommonUtils.spannableEmoticonFilter(viewHolder.content,sessionBean.getContent());
            }
            CLImageLoadUtil.loadRoundImg(viewHolder.avatar,sessionBean.getAvatarUrl(),R.drawable.avatar,20);
            viewHolder.nickName.setText(sessionBean.getName());
            viewHolder.time.setText(sessionBean.getSendTime());

            if (sessionBean.getUnreadNumber() > 0){
                viewHolder.unread_count.setText("" + sessionBean.getUnreadNumber());
                viewHolder.unread_count.setVisibility(View.VISIBLE);
            }else {
                viewHolder.unread_count.setVisibility(View.GONE);
            }


            return view;
        }
    }

    private static class ViewHolder{
        /// 群头像布局
        LinearLayout group_avatar_layout;
        /// 单聊头像布局
        LinearLayout singnle_avatar_layout;
        /// 头像
        ImageView avatar;
        /// 昵称
        TextView nickName;
        /// 内容
        TextView content;
        /// 时间
        TextView time;
        /// 未读数
        TextView unread_count;
    }
}
