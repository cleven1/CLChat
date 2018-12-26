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

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.API.OkGoUtil;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.manager.CLMQTTManager;
import com.cleven.clchat.model.CLUserBean;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLImageLoadUtil;
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
    private ArrayList<CLUserBean> userList = new ArrayList<>();
    private MyFriendListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contack_layout);
        setupTitleBar();
        findView();

    }
    private void setupTitleBar(){
        titleBar = (CommonTitleBar) findViewById(R.id.titlebar);
        ImageButton leftImageButton = titleBar.getLeftImageButton();
        leftImageButton.setVisibility(View.VISIBLE);
        rightTextView = titleBar.getRightTextView();
        rightTextView.setVisibility(View.GONE);
        centerTextView = titleBar.getCenterTextView();
        centerTextView.setText("添加好友");
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
                params.put("user_id",keyword);
                OkGoUtil.getRequets(CLAPPConst.HTTP_SERVER_BASE_URL + "friend/queryFriend", "addFriend", params, new OkGoUtil.CLNetworkCallBack() {
                    @Override
                    public void onSuccess(Map response) {
                        LogPrintUtils.eTag("查询好友",response.toString());
                        List<Map> users = (List) response.get("data");
                        for (int i = 0; i < users.size(); i++){
                            userList.add(JSON.parseObject(String.valueOf(users.get(i)),CLUserBean.class));
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
            final CLUserBean userBean = userList.get(position);
            CLImageLoadUtil.loadRoundImg(viewHolder.avatar,userBean.getAvatarUrl(),R.drawable.avatar,20);
            viewHolder.name.setText(userBean.getName());
            viewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /// 通过MQTT发送添加好友
                    CLMQTTManager.getInstance().sendAddFriendMessage(userBean.getUserId());
                    viewHolder.addFriend.setText("已发送");
                    viewHolder.addFriend.setBackgroundResource(R.drawable.addfriend_text_add_shape);
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
