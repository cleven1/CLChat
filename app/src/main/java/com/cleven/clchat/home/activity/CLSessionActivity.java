package com.cleven.clchat.home.activity;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.home.adapter.CLSessionRecyclerAdapter;
import com.cleven.clchat.manager.CLMessageManager;
import com.cleven.clchat.utils.CLAPPConst;
import com.lqr.audio.AudioPlayManager;
import com.lqr.audio.AudioRecordManager;
import com.lqr.audio.IAudioRecordListener;
import com.lqr.emoji.EmotionKeyboard;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionSelectedListener;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.ui.ImagePreviewActivity;
import com.wuhenzhizao.titlebar.utils.KeyboardConflictCompat;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CLSessionActivity extends CLBaseActivity implements IEmotionSelectedListener,TextView.OnEditorActionListener{
    private LinearLayout mLlRoot;
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
    /// 相册
    private ImageView mIvAlbum;
    /// 拍照
    private ImageView mIvShot;
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
    private static final int IMAGE_PICKER = 100;
    private int audioDuration = 0;
    private String mediaUrl;

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

        initAudioRecord();
    }
    private void initAudioRecord() {
        AudioRecordManager.getInstance(this).setMaxVoiceDuration(CLAPPConst.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND);
        File mAudioDir = new File(CLAPPConst.AUDIO_SAVE_DIR);
        /// 判断文件夹是否存在,不存在创建
        if (!mAudioDir.exists()){
            mAudioDir.mkdirs();
        }
        AudioRecordManager.getInstance(this).setAudioSavePath(mAudioDir.getAbsolutePath());
        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {

            private TextView mTimerTV;
            private TextView mStateTV;
            private ImageView mStateIV;
            private PopupWindow mRecordWindow;

            @Override
            public void initTipView() {
                View view = View.inflate(CLSessionActivity.this, R.layout.popup_audio_wi_vo, null);
                mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
                mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
                mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
                mRecordWindow = new PopupWindow(view, -1, -1);
                mRecordWindow.showAtLocation(mLlRoot, 17, 0, 0);
                mRecordWindow.setFocusable(true);
                mRecordWindow.setOutsideTouchable(false);
                mRecordWindow.setTouchable(false);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.GONE);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
                    this.mTimerTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (this.mRecordWindow != null) {
                    mStateIV.setImageResource(R.mipmap.ic_volume_wraning);
                    mStateTV.setText(R.string.voice_short);
                }
            }

            @Override
            public void setCancelTipView() {
                if (this.mRecordWindow != null) {
                    this.mTimerTV.setVisibility(View.GONE);
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_cancel);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_cancel);
                    this.mStateTV.setBackgroundResource(R.drawable.corner_voice_style);
                }
            }

            @Override
            public void destroyTipView() {
                if (this.mRecordWindow != null) {
                    this.mRecordWindow.dismiss();
                    this.mRecordWindow = null;
                    this.mStateIV = null;
                    this.mStateTV = null;
                    this.mTimerTV = null;
                }
            }

            @Override
            public void onStartRecord() {
               Toast.makeText(CLSessionActivity.this,"开始录制",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                if (audioPath == null){
                    audioDuration = duration;
                    sendMessage();
                    Toast.makeText(CLSessionActivity.this,"录音文件保存失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                //发送文件
                File file = new File(audioPath.getPath());
                if (file.exists()) {
//                    mPresenter.sendAudioFile(audioPath, duration);
                    Toast.makeText(CLSessionActivity.this,"录制完成 时长 = " + duration,Toast.LENGTH_SHORT).show();
                }
                mediaUrl = audioPath.getPath();
                audioDuration = duration;
                sendMessage();
            }

            @Override
            public void onAudioDBChanged(int db) {
                switch (db / 5) {
                    case 0:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                        break;
                    case 1:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_2);
                        break;
                    case 2:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_3);
                        break;
                    case 3:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_4);
                        break;
                    case 4:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_5);
                        break;
                    case 5:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_6);
                        break;
                    case 6:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_7);
                        break;
                    default:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_8);
                }
            }
        });
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
        mLlRoot = (LinearLayout) findViewById(R.id.llRoot);
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
        mIvAlbum = (ImageView) findViewById(R.id.ivAlbum);
        mIvShot = (ImageView) findViewById(R.id.ivShot);

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
        // 录音事件
        mBtnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AudioRecordManager.getInstance(CLSessionActivity.this).startRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCancelled(view, motionEvent)) {
                            AudioRecordManager.getInstance(CLSessionActivity.this).willCancelRecord();
                        } else {
                            AudioRecordManager.getInstance(CLSessionActivity.this).continueRecord();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        AudioRecordManager.getInstance(CLSessionActivity.this).stopRecord();
                        AudioRecordManager.getInstance(CLSessionActivity.this).destroyRecord();
                        break;
                }
                return false;
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

        /// 相册
        mIvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CLSessionActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
                Toast.makeText(CLSessionActivity.this,"相册",Toast.LENGTH_SHORT).show();
            }
        });
        /// 相机
        mIvShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CLSessionActivity.this,"相机",Toast.LENGTH_SHORT).show();
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

    /// 图片选择回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

                Log.e("CSDN_LQR", isOrig ? "发原图" : "不发原图");//若不发原图的话，需要在自己在项目中做好压缩图片算法
                for (ImageItem imageItem : images) {
                    Log.e("CSDN_LQR", imageItem.path);
                }
            }
        }
    }

    /// 判断录音事件是否取消
    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    private void sendMessage(){
        CLMessageBodyType messageBodyType = CLMessageBodyType.MessageBodyType_Text;
        String content = "";
        int duration = 0;
        String url = "";
        if (mEtContent.getText().toString().length() > 0){
            messageBodyType = CLMessageBodyType.MessageBodyType_Text;
            content = mEtContent.getText().toString().trim();
            mEtContent.setText("");
        }else if (audioDuration > 0){
            messageBodyType = CLMessageBodyType.MessageBodyType_Voice;
            duration = audioDuration;
            audioDuration = 0;
        }else if (mediaUrl != null){
            messageBodyType = CLMessageBodyType.MessageBodyType_Image;
            url = mediaUrl;
            mediaUrl = null;
        }
        CLMessageBean messageBean = CLMessageManager.getInstance().sendMessage(content,mUserId,messageBodyType,duration,url);
        messageList.add(messageBean);
        /// 插入并刷新
        adapter.notifyItemInserted(messageList.size());
        /// 滚到最后一个位置
        mRvSessionView.scrollToPosition(messageList.size() - 1);

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

    // 监听新消息的到来
    private void obseverReceiveMessage(){
        CLMessageManager.getInstance().setReceiveMessageOnListener(new CLMessageManager.CLReceiveMessageOnListener() {
            @Override
            public void onMessage(final CLMessageBean message) {
                String targetUserId = message.getTargetId();
                /// 判断收到的消息是否是发给当前会话的
                if (!targetUserId.equals(mUserId)){
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageList.add(message);
                        /// 插入并刷新
                        adapter.notifyItemInserted(messageList.size());
                        /// 滚到最后一个位置
                        mRvSessionView.scrollToPosition(messageList.size() - 1);
                    }
                });
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /// 退出停止播放
        AudioPlayManager.getInstance().stopPlay();
    }
}
