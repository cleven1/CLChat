package com.cleven.clchat.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.adapter.CLSessionRecyclerAdapter;
import com.cleven.clchat.manager.CLMessageManager;
import com.lqr.emoji.EmotionKeyboard;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.wuhenzhizao.titlebar.utils.KeyboardConflictCompat;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

public class CLSessionActivity extends CLBaseActivity implements IEmotionSelectedListener,TextView.OnEditorActionListener {
    /// 消息列表
    private RecyclerView mRvSessionView;
    /// 录音按钮
    private ImageView mIvAudio;
    /// 输入文本
    private EditText mEtContent;
    /// 按住说话
    private Button mBtnAudio;
    /// 表情
    private ImageView mIvEmo;
    /// 更多
    private ImageView mIvMore;
    /// 发送按钮
    private Button mBtnSend;
    /// 表情和更多父view
    private FrameLayout mFlEmotionView;
    /// 表情
    private EmotionLayout mElEmotion;
    /// 更多
    private LinearLayout mLlMore;
    /// 键盘管理
    private EmotionKeyboard mEmotionKeyboard;
    /// 数据源
    private List<CLMessageBean> messageList;
    /// 整个内容的父视图
    private LinearLayout mLlContent;
    private CommonTitleBar titleBar;
    private CLSessionRecyclerAdapter adapter;
    private String mUserName;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        getIntentParams();

        findViews();

        setupTitleBar();

        initListener();
        /// 监听新消息
        obseverReceiveMessage();
    }

    /// 处理titleBar遮挡键盘的问题
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyboardConflictCompat.assistWindow(getWindow());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEtContent.clearFocus();
    }

    public void getIntentParams() {
        mUserName = getIntent().getStringExtra("userName");
        mUserId = getIntent().getStringExtra("userId");

    }

    private void setupTitleBar(){
        /// 点击返回按钮
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    onBackPressed();
                }
            }
        });

        TextView centerTextView = titleBar.getCenterTextView();
        centerTextView.setText(mUserName);

    }

    private void findViews() {
        mLlContent = (LinearLayout) findViewById(R.id.llContent);
        mRvSessionView = (RecyclerView)findViewById( R.id.rv_sessionView );
        mIvAudio = (ImageView)findViewById( R.id.ivAudio );
        mEtContent = (EditText)findViewById( R.id.etContent );
        mBtnAudio = (Button)findViewById( R.id.btnAudio );
        mIvEmo = (ImageView)findViewById( R.id.ivEmo );
        mIvMore = (ImageView)findViewById( R.id.ivMore );
        mBtnSend = (Button)findViewById( R.id.btnSend );
        mFlEmotionView = (FrameLayout)findViewById( R.id.flEmotionView );
        mElEmotion = (EmotionLayout)findViewById( R.id.elEmotion );
        mLlMore = (LinearLayout)findViewById( R.id.llMore );
        titleBar = (CommonTitleBar) findViewById(R.id.titlebar);

        /// 实现输入框图文混排
        mElEmotion.attachEditText(mEtContent);
        initEmotionKeyboard();
        /// 设置键盘发送按钮监听
        mEtContent.setOnEditorActionListener(this);

        /// 初始化数据源
        messageList = new ArrayList<>();

        // 设置适配器
        adapter = new CLSessionRecyclerAdapter(this, messageList);
        mRvSessionView.setAdapter(adapter);
        mRvSessionView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

    }

    public void initListener() {


        mElEmotion.setEmotionSelectedListener(this);
        mElEmotion.setEmotionAddVisiable(true);
        mElEmotion.setEmotionSettingVisiable(true);
        mElEmotion.setEmotionExtClickListener(new IEmotionExtClickListener() {
            @Override
            public void onEmotionAddClick(View view) {
                Toast.makeText(getApplicationContext(), "add", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEmotionSettingClick(View view) {
                Toast.makeText(getApplicationContext(), "setting", Toast.LENGTH_SHORT).show();
            }
        });
        mLlContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        closeBottomAndKeyboard();
                        break;
                }
                return false;
            }
        });
        mIvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtnAudio.isShown()) {
                    hideAudioButton();
                    mEtContent.requestFocus();
                    if (mEmotionKeyboard != null) {
                        mEmotionKeyboard.showSoftInput();
                    }
                } else {
                    showAudioButton();
                    hideEmotionLayout();
                    hideMoreLayout();
                }
            }
        });

        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtContent.getText().toString().trim().length() > 0) {
                    mBtnSend.setVisibility(View.VISIBLE);
                    mIvMore.setVisibility(View.GONE);
                } else {
                    mBtnSend.setVisibility(View.GONE);
                    mIvMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /// 发送事件
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage(){
        CLMessageBean messageBean = CLMessageManager.getInstance().sendMessage(mEtContent.getText().toString().trim(),mUserId);
        messageList.add(messageBean);
        /// 插入并刷新
        adapter.notifyItemInserted(messageList.size());
        /// 滚到最后一个位置
        mRvSessionView.scrollToPosition(messageList.size() - 1);
        mEtContent.setText("");
        /// 发送回调
        CLMessageManager.getInstance().setMessageStatusOnListener(new CLMessageManager.CLSendMessageStatusOnListener() {
            @Override
            public void onSuccess(CLMessageBean message) {
                updateMessageStatus(message);
            }
            @Override
            public void onFailure(CLMessageBean message) {
                updateMessageStatus(message);
            }
        });

    }

    private void obseverReceiveMessage(){
        CLMessageManager.getInstance().setReceiveMessageOnListener(new CLMessageManager.CLReceiveMessageOnListener() {
            @Override
            public void onMessage(CLMessageBean message) {
                messageList.add(message);
                /// 插入并刷新
                adapter.notifyItemInserted(messageList.size());
                /// 滚到最后一个位置
                mRvSessionView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    /// 发送成功后更新状态
    private void updateMessageStatus(CLMessageBean message){
        if (messageList.size() == 1){
            CLMessageBean bean = messageList.get(0);
            bean.setSendStatus(message.getSendStatus());
            adapter.notifyItemChanged(0);
        }else {
            for (int i = messageList.size() - 1; i > 0; i--) {
                CLMessageBean bean = messageList.get(i);
                if (bean.getMessageId().equals(message.getMessageId())){
                    bean.setSendStatus(message.getSendStatus());
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_NULL:
                System.out.println("Done_content: " + v.getText() );
                break;
            case EditorInfo.IME_ACTION_SEND:
                sendMessage();
                break;
            case EditorInfo.IME_ACTION_DONE:
                System.out.println("action done for number_content: "  + v.getText());
                break;
        }

        return true;
    }

    private void initEmotionKeyboard() {
        mEmotionKeyboard = EmotionKeyboard.with(this);
        mEmotionKeyboard.bindToEditText(mEtContent);
        mEmotionKeyboard.bindToContent(mLlContent);
        mEmotionKeyboard.setEmotionLayout(mFlEmotionView);
        mEmotionKeyboard.bindToEmotionButton(mIvEmo, mIvMore);
        mEmotionKeyboard.setOnEmotionButtonOnClickListener(new EmotionKeyboard.OnEmotionButtonOnClickListener() {
            @Override
            public boolean onEmotionButtonOnClickListener(View view) {
                switch (view.getId()) {
                    case R.id.ivEmo:
                        if (!mElEmotion.isShown()) {
                            if (mLlMore.isShown()) {
                                showEmotionLayout();
                                hideMoreLayout();
                                hideAudioButton();
                                return true;
                            }
                        } else if (mElEmotion.isShown() && !mLlMore.isShown()) {
                            mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
                            return false;
                        }
                        showEmotionLayout();
                        hideMoreLayout();
                        hideAudioButton();
                        break;
                    case R.id.ivMore:
                        if (!mLlMore.isShown()) {
                            if (mElEmotion.isShown()) {
                                showMoreLayout();
                                hideEmotionLayout();
                                hideAudioButton();
                                return true;
                            }
                        }
                        showMoreLayout();
                        hideEmotionLayout();
                        hideAudioButton();
                        break;
                }
                return false;
            }
        });
    }

    private void showAudioButton() {
        mBtnAudio.setVisibility(View.VISIBLE);
        mEtContent.setVisibility(View.GONE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_keyboard);

        if (mFlEmotionView.isShown()) {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.interceptBackPress();
            }
        } else {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.hideSoftInput();
            }
        }
    }

    private void hideAudioButton() {
        mBtnAudio.setVisibility(View.GONE);
        mEtContent.setVisibility(View.VISIBLE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_voice);
    }

    private void showEmotionLayout() {
        mElEmotion.setVisibility(View.VISIBLE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_keyboard);
    }

    private void hideEmotionLayout() {
        mElEmotion.setVisibility(View.GONE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
    }

    private void showMoreLayout() {
        mLlMore.setVisibility(View.VISIBLE);
    }

    private void hideMoreLayout() {
        mLlMore.setVisibility(View.GONE);
    }

    private void closeBottomAndKeyboard() {
        mElEmotion.setVisibility(View.GONE);
        mLlMore.setVisibility(View.GONE);
        if (mEmotionKeyboard != null) {
            mEmotionKeyboard.interceptBackPress();
        }
    }

    @Override
    public void onBackPressed() {
        if (mElEmotion.isShown() || mLlMore.isShown()) {
            mEmotionKeyboard.interceptBackPress();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEmojiSelected(String key) {
        Log.e("CSDN_LQR", "onEmojiSelected : " + key);
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
        Toast.makeText(getApplicationContext(), stickerBitmapPath, Toast.LENGTH_SHORT).show();
        Log.e("CSDN_LQR", "stickerBitmapPath : " + stickerBitmapPath);
    }


}
