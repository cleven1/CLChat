package com.cleven.clchat.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseFragment;
import com.cleven.clchat.contack.activity.CLAddFriendActivity;
import com.cleven.clchat.fragment.contack.adpater.ContactsAdapter;
import com.cleven.clchat.fragment.contack.views.PinnedHeaderDecoration;
import com.cleven.clchat.model.CLUserBean;
import com.nanchen.wavesidebar.SearchEditText;
import com.nanchen.wavesidebar.Trans2PinYinUtil;
import com.nanchen.wavesidebar.WaveSideBarView;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cleven on 2018/12/11.
 */

public class CLContactFragment extends CLBaseFragment {

    private List<CLUserBean> mContactModels;
    private List<CLUserBean> mShowModels;
    private RecyclerView mRecyclerView;
    private WaveSideBarView mWaveSideBarView;
    private SearchEditText mSearchEditText;
    private ContactsAdapter mAdapter;
    private CommonTitleBar titleBar;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.contact_layout,null);

        bindView(view);

        return view;
    }
    ///设置titleBar
    public void setTitleBar(CommonTitleBar titleBar) {
        this.titleBar = titleBar;
        TextView rightTextView = titleBar.getRightTextView();
        rightTextView.setText("➕");
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_RIGHT_TEXT){
                    Intent intent = new Intent(mContext,CLAddFriendActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    private void bindView(View view) {

        // RecyclerView设置相关
        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        final PinnedHeaderDecoration decoration = new PinnedHeaderDecoration();
        decoration.registerTypePinnedHeader(1, new PinnedHeaderDecoration.PinnedHeaderCreator() {
            @Override
            public boolean create(RecyclerView parent, int adapterPosition) {
                return true;
            }
        });
        mRecyclerView.addItemDecoration(decoration);

        // 侧边设置相关
        mWaveSideBarView = (WaveSideBarView) view.findViewById(R.id.main_side_bar);
        mWaveSideBarView.setTextColor(R.color.black);
        mWaveSideBarView.setOnSelectIndexItemListener(new WaveSideBarView.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String letter) {
                for (int i=0; i<mContactModels.size(); i++) {
                    if (mContactModels.get(i).getIndex().equals(letter)) {
                        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });


        // 搜索按钮相关
        mSearchEditText = (SearchEditText) view.findViewById(R.id.main_search);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mShowModels.clear();
                for (CLUserBean model : mContactModels) {
                    String str = Trans2PinYinUtil.trans2PinYin(model.getName());
                    if (str.contains(s.toString()) || model.getName().contains(s.toString())) {
                        mShowModels.add(model);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        mContactModels = new ArrayList<>();
        mShowModels = new ArrayList<>();
        CLUserBean userBean = new CLUserBean();
        userBean.setName("a");
        userBean.setUnreadNum(0);
        mContactModels.add(userBean);

        mShowModels.addAll(mContactModels);
        mAdapter = new ContactsAdapter(mContext,mShowModels);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContactModels != null){
            mContactModels.clear();
            mContactModels = null;
        }
    }
}
