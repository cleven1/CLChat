package com.cleven.clchat.home.CLEmojiCommon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.cleven.clchat.R;
import com.cleven.clchat.home.CLEmojiCommon.adapter.AppsAdapter;
import com.cleven.clchat.home.CLEmojiCommon.data.AppBean;

import java.util.ArrayList;

public class CLKeyBoardMoreGridView extends RelativeLayout {

    private final Context mConetext;
    protected View view;

    /// item点击回调
    private CLMoreItemClickOnListener moreItemClickOnListener;
    public void setMoreItemClickOnListener(CLMoreItemClickOnListener moreItemClickOnListener) {
        this.moreItemClickOnListener = moreItemClickOnListener;
    }
    public interface CLMoreItemClickOnListener{
        void didItem(int postion);
    }

    public CLKeyBoardMoreGridView(Context context) {
        this(context, null);
    }

    public CLKeyBoardMoreGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mConetext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_apps, this);
        init();
    }

    protected void init(){
        GridView gv_apps = (GridView) view.findViewById(R.id.gv_apps);
        ArrayList<AppBean> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppBean(R.mipmap.ic_func_pic, "图片"));
        mAppBeanList.add(new AppBean(R.mipmap.ic_func_shot, "拍摄"));
        mAppBeanList.add(new AppBean(R.mipmap.ic_func_location, "位置"));
        mAppBeanList.add(new AppBean(R.mipmap.ic_func_red_pack, "红包"));
        mAppBeanList.add(new AppBean(0, ""));
        AppsAdapter adapter = new AppsAdapter(mConetext,mAppBeanList);
        adapter.setMoreItemClickOnListener(new AppsAdapter.CLMoreItemClickOnListener() {
            @Override
            public void didItem(int postion) {
                if (moreItemClickOnListener != null){
                    moreItemClickOnListener.didItem(postion);
                }
            }
        });
        gv_apps.setAdapter(adapter);
    }
}
