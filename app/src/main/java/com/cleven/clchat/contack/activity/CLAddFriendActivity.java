package com.cleven.clchat.contack.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.model.CLUserBean;
import com.nanchen.wavesidebar.SearchEditText;

import java.util.ArrayList;

import dev.utils.LogPrintUtils;

public class CLAddFriendActivity extends CLBaseActivity {

    private SearchEditText mSearchEditText;
    private ListView mListView;
    private ArrayList<CLUserBean> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contack_layout);

        findView();

    }

    private void findView(){
        // 搜索按钮相关
        mSearchEditText = (SearchEditText) findViewById(R.id.friend_search);
        mSearchEditText.setPressed(true);
        mSearchEditText.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view, String keyword) {
                LogPrintUtils.eTag("keyword",keyword);
            }
        });
        mListView = findViewById(R.id.listView);
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
            ViewHolder viewHolder;
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
            CLUserBean userBean = userList.get(position);
            Glide.with(CLAddFriendActivity.this).load(userBean.getAvatarUrl()).into(viewHolder.avatar);
            viewHolder.name.setText(userBean.getName());
            viewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CLAddFriendActivity.this,"添加好友",Toast.LENGTH_SHORT).show();
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
