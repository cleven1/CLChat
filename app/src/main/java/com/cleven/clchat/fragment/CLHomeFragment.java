package com.cleven.clchat.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.home.activity.CLSessionActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLHomeFragment extends CLBaseFragment {

    private ListView listView;
    private List<String> dataArray;

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.session_listview,null);
        listView = (ListView) view.findViewById(R.id.listView);

        dataArray = new ArrayList<>();
        dataArray.add("session 1");
        dataArray.add("session 2");
        dataArray.add("session 3");

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        //设置适配器
        listView.setAdapter(new MyAdapter());

        listView.setOnItemClickListener(new MyOnItemClickListener());
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(mContext,"i == " + i,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext,CLSessionActivity.class);
            mContext.startActivity(intent);
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
                viewHolder.avatar = view.findViewById(R.id.iv_avatar);
                viewHolder.nickName = view.findViewById(R.id.tv_title);
                viewHolder.content = view.findViewById(R.id.tv_content);
                viewHolder.time = view.findViewById(R.id.tv_time);
                view.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) view.getTag();
            }

            String str = dataArray.get(i);
            viewHolder.content.setText(str);

            return view;
        }
    }

    private static class ViewHolder{
        ImageView avatar;
        TextView nickName;
        TextView content;
        TextView time;
    }
}
