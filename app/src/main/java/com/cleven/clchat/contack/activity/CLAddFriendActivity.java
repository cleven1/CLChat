package com.cleven.clchat.contack.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.API.OkGoUtil;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.contack.bean.CLFriendBean;
import com.cleven.clchat.contack.bean.CLNewFriendBean;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.manager.CLMQTTManager;
import com.cleven.clchat.manager.CLMessageManager;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLHUDUtil;
import com.cleven.clchat.utils.CLImageLoadUtil;
import com.cleven.clchat.utils.CLJsonUtil;
import com.lzy.okgo.model.HttpParams;
import com.nanchen.wavesidebar.SearchEditText;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.utils.LogPrintUtils;

public class CLAddFriendActivity extends CLBaseActivity {
    private TextView centerTextView;
    private CommonTitleBar titleBar;
    private TextView rightTextView;
    private SearchEditText mSearchEditText;
    private ListView mListView;
    private ArrayList<CLNewFriendBean> userList = new ArrayList<>();
    private MyFriendListViewAdapter adapter;
    private String title;
    private boolean isAddFriend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getIntent().getStringExtra("title");
        if (title.equals("好友通知")){
            isAddFriend = true;
        }
        setContentView(R.layout.add_contack_layout);
        setupTitleBar();
        findView();

        if (isAddFriend){
            initData();
        }

    }

    private void initData() {
        List<CLNewFriendBean> allNewFriendData = CLNewFriendBean.getAllNewFriendData();
        userList.addAll(allNewFriendData);
        adapter.notifyDataSetChanged();
        /// 更新所有的状态
        for (CLNewFriendBean friendBean : allNewFriendData){
            friendBean.setGotoDetail(true);
            CLNewFriendBean.updateData(friendBean);
        }
    }

    private void setupTitleBar(){
        titleBar = (CommonTitleBar) findViewById(R.id.titlebar);
        ImageButton leftImageButton = titleBar.getLeftImageButton();
        leftImageButton.setVisibility(View.VISIBLE);
        rightTextView = titleBar.getRightTextView();
        rightTextView.setVisibility(View.GONE);
        centerTextView = titleBar.getCenterTextView();
        centerTextView.setText(title);
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON){
                    onBackPressed();
                }
            }
        });
    }
    private void findView(){

        // 搜索按钮相关
        mSearchEditText = (SearchEditText) findViewById(R.id.friend_search);
        mSearchEditText.setPressed(true);
        mSearchEditText.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view, String keyword) {
                LogPrintUtils.eTag("keyword",keyword);
                HttpParams params = new HttpParams();
                params.put("keyword",keyword);
                params.put("user_id",CLUserManager.getInstence().getUserInfo().getUserId());
                OkGoUtil.getRequets(CLAPPConst.HTTP_SERVER_BASE_URL + "friend/queryFriend", "queryFriend", params, new OkGoUtil.CLNetworkCallBack() {
                    @Override
                    public void onSuccess(Map response) {
                        LogPrintUtils.eTag("查询好友",response.toString());
                        List<Map> users = (List) response.get("data");
                        for (int i = 0; i < users.size(); i++){
                            CLNewFriendBean friendBean = CLJsonUtil.parseJsonToObj(String.valueOf(users.get(i)),CLNewFriendBean.class);
                            userList.add(friendBean);
                            CLAddFriendActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(final Map error) {
                        LogPrintUtils.eTag("查询好友 error",error.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CLAddFriendActivity.this, String.valueOf(error.get("error_msg")),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        /// 添加好友界面,隐藏搜索
        if (isAddFriend){
            mSearchEditText.setVisibility(View.GONE);
        }

        mListView = findViewById(R.id.listView);
        adapter = new MyFriendListViewAdapter();
        mListView.setAdapter(adapter);

    }

    private class MyFriendListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = View.inflate(CLAddFriendActivity.this,R.layout.friend_item_layout,null);
                viewHolder.avatar = convertView.findViewById(R.id.avatar);
                viewHolder.name = convertView.findViewById(R.id.name);
                viewHolder.addFriend = convertView.findViewById(R.id.addFriend);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final CLNewFriendBean friendBean = userList.get(position);
            CLImageLoadUtil.loadRoundImg(viewHolder.avatar,friendBean.getAvatarUrl(),R.drawable.avatar,20);
            viewHolder.name.setText(friendBean.getName());
            if (isAddFriend && friendBean.isFriend()){
                viewHolder.addFriend.setText("已添加");
                viewHolder.addFriend.setBackgroundResource(R.drawable.addfriend_text_add_shape);
                viewHolder.addFriend.setEnabled(false);
            }else if (isAddFriend && friendBean.isFriend() == false){
                viewHolder.addFriend.setText("添加");
                viewHolder.addFriend.setEnabled(true);
            }else {
                viewHolder.addFriend.setText("加好友");
                viewHolder.addFriend.setEnabled(true);
            }
            viewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAddFriend){
                        HttpParams params = new HttpParams();
                        params.put("target_user_id",friendBean.getUserId());
                        params.put("user_id",CLUserManager.getInstence().getUserInfo().getUserId());
                        OkGoUtil.postRequest(CLAPPConst.HTTP_SERVER_BASE_URL + "friend/addFriend", "addFriend", params, new OkGoUtil.CLNetworkCallBack() {
                            @Override
                            public void onSuccess(Map response) {
                                friendBean.setFriend(true);
                                adapter.notifyDataSetChanged();
                                viewHolder.addFriend.setText("已添加");
                                viewHolder.addFriend.setBackgroundResource(R.drawable.addfriend_text_add_shape);
                                viewHolder.addFriend.setEnabled(false);
                                /// 更新数据库
                                CLNewFriendBean.updateData(friendBean);
                                /// 发送消息通知好友
                                String name = CLUserManager.getInstence().getUserInfo().getName();
                                CLMQTTManager.getInstance().sendAddFriendMessage(friendBean.getUserId());
                                CLMessageManager.getInstance().sendTextMessage(name + "已经和你成为好友了",friendBean.getUserId(),false);

                                /// 插入好友数据库
                                String json = CLJsonUtil.parseObjToJson(friendBean);
                                CLFriendBean bean = CLJsonUtil.parseJsonToObj(json, CLFriendBean.class);
                                bean.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());
                                CLFriendBean.updateData(bean);
                                /// 插入消息会话列表
                                CLMessageManager.getInstance().SessionMessageHandler(CLMessageBean.friendToMessage(bean));

                                /// 回调
                                setResult(CLAPPConst.ADDFRIENDRESULTCODE);

                            }

                            @Override
                            public void onFailure(Map error) {
                                Toast.makeText(CLAddFriendActivity.this, "" + error.get("error_msg"),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        HttpParams params = new HttpParams();
                        params.put("target_user_id",friendBean.getUserId());
                        params.put("user_id",CLUserManager.getInstence().getUserInfo().getUserId());
                        OkGoUtil.getRequets(CLAPPConst.HTTP_SERVER_BASE_URL + "friend/checkFriend", "checkFriend", params, new OkGoUtil.CLNetworkCallBack() {
                            @Override
                            public void onSuccess(Map response) {
                                CLHUDUtil.showErrorHUD(CLAddFriendActivity.this,response.get("error_msg").toString());
                            }
                            @Override
                            public void onFailure(Map error) {
                                /// 不是好友
                                if (error.get("error_code").equals("4002")){
                                    /// 通过MQTT发送添加好友
                                    CLMQTTManager.getInstance().sendnviteFriendMessage(friendBean.getUserId());
                                    viewHolder.addFriend.setText("已发送");
                                    viewHolder.addFriend.setBackgroundResource(R.drawable.addfriend_text_add_shape);
                                    viewHolder.addFriend.setEnabled(false);
                                }else {
                                    CLHUDUtil.showErrorHUD(CLAddFriendActivity.this,error.get("error_msg").toString());
                                }
                            }
                        });
                    }
                }
            });

            return convertView;
        }

    }

    private static class ViewHolder{
        ImageView avatar;
        TextView name;
        TextView addFriend;
    }
}
