package com.cleven.clchat.home.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cleven.clchat.R;
import com.cleven.clchat.base.CLBaseActivity;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.home.Bean.CLSendStatus;
import com.cleven.clchat.home.Bean.CLUploadStatus;
import com.cleven.clchat.home.CLEmojiCommon.utils.CLEmojiCommonUtils;
import com.cleven.clchat.home.CLEmojiCommon.widget.CLKeyBoardMoreGridView;
import com.cleven.clchat.home.adapter.CLSessionRecyclerAdapter;
import com.cleven.clchat.manager.CLMessageManager;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLFileUtils;
import com.lqr.audio.AudioPlayManager;
import com.lqr.audio.AudioRecordManager;
import com.lqr.audio.IAudioRecordListener;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.ui.ImagePreviewActivity;
import com.sj.emoji.EmojiBean;
import com.wuhenzhizao.titlebar.utils.KeyboardConflictCompat;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dev.utils.app.image.BitmapUtils;
import pub.devrel.easypermissions.EasyPermissions;
import sj.keyboard.XhsEmoticonsKeyBoard;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.EmoticonsEditText;
import sj.keyboard.widget.FuncLayout;

public class CLSessionActivity extends CLBaseActivity implements FuncLayout.OnFuncKeyBoardListener {
    /// 消息列表
    private RecyclerView mRvSessionView;
    /// 表情管理键盘
    private XhsEmoticonsKeyBoard ekBar;
    /// 数据源
    private List<CLMessageBean> messageList;
    private CommonTitleBar titleBar;
    private CLSessionRecyclerAdapter adapter;
    private String mUserName;
    private String mUserId;
    private boolean mIsGroup;
    private static final int IMAGE_PICKER = 100;
    private CLKeyBoardMoreGridView mKeyboardMoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        initPickImage();

        getIntentParams();

        findViews();

        initEmoticonsKeyBoardBar();

        setupTitleBar();

        initListener();
        /// 监听新消息
        obseverReceiveMessage();

        initAudioRecord();

        initData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /// 退出停止播放
        AudioRecordManager.getInstance(this).stopRecord();
        AudioPlayManager.getInstance().stopPlay();
        /// 回调
        setResult(CLAPPConst.SESSIONMESSAGERESULTCODE);
    }

    private void initData() {
        /// 在数据库中获取历史聊天信息
        messageList = CLMessageBean.getUserMessageData(mUserId);

        // 设置适配器
        adapter = new CLSessionRecyclerAdapter(this, messageList);
        mRvSessionView.setAdapter(adapter);
        mRvSessionView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        scrollToBottom();
        /// 更新状态
        CLMessageBean.updateRecviveAllMessageStatus(mUserId);
        /// 回调
        setResult(CLAPPConst.SESSIONMESSAGERESULTCODE);
        /// 监听点击重发按钮
        adapter.setMessageSendFailListener(new CLSessionRecyclerAdapter.CLMessageSendFailListener() {
            @Override
            public void onRetry(CLMessageBean messageBean) {
                CLMessageBodyType messageBodyType = CLMessageBodyType.fromTypeName(messageBean.getMessageType());
                CLUploadStatus uploadStatus = CLUploadStatus.fromTypeName(messageBean.getUploadStatus());
                switch (messageBodyType){
                    case MessageBodyType_Text:
                        CLMessageManager.getInstance().sendTextMessage(messageBean.getContent(),mUserId,messageBean.isGroupSession());
                        break;
                    case MessageBodyType_Voice:
                        if (uploadStatus == CLUploadStatus.UploadStatus_fail){
                            CLMessageManager.getInstance().sendAudioMessage(messageBean.getLocalUrl(),messageBean.getDuration(),mUserId,messageBean.isGroupSession(),true);
                        }else {
                            CLMessageManager.getInstance().sendAudioMessage(messageBean.getLocalUrl(),messageBean.getDuration(),mUserId,messageBean.isGroupSession(),false);
                        }
                        break;
                    case MessageBodyType_Image:
                        if (uploadStatus == CLUploadStatus.UploadStatus_fail){
                            CLMessageManager.getInstance().sendImageMessage(messageBean.getLocalUrl(),messageBean.getMediaUrl(),messageBean.getWitdh(),messageBean.getHeight(),mUserId,messageBean.isGroupSession(),true);
                        }else {
                            CLMessageManager.getInstance().sendImageMessage(messageBean.getLocalUrl(),messageBean.getMediaUrl(),messageBean.getWitdh(),messageBean.getHeight(),mUserId,messageBean.isGroupSession(),false);
                        }
                        break;
                    case MessageBodyType_Video:
                        if (uploadStatus == CLUploadStatus.UploadStatus_fail){
                            CLMessageManager.getInstance().sendVideoMessage(messageBean.getLocalUrl(),messageBean.getVideoThumbnail(),messageBean.getDuration(),mUserId,messageBean.isGroupSession(),true);
                        }else {
                            CLMessageManager.getInstance().sendVideoMessage(messageBean.getLocalUrl(),messageBean.getVideoThumbnail(),messageBean.getDuration(),mUserId,messageBean.isGroupSession(),false);
                        }
                        break;
                }
            }
        });

    }

    private void initPickImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setSelectLimit(9);    //选中数量限制
    }

    private void initEmoticonsKeyBoardBar() {
        CLEmojiCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(CLEmojiCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        mKeyboardMoreView = new CLKeyBoardMoreGridView(this);
        ekBar.addFuncView(mKeyboardMoreView);

    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                CLEmojiCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if(o == null){
                    return;
                }
                if(actionType == CLEmojiCommonUtils.EMOTICON_CLICK_BIGIMAGE){
                    if(o instanceof EmoticonEntity){
                        OnSendImage(((EmoticonEntity)o).getIconUri());
                    }
                } else {
                    String content = null;
                    if(o instanceof EmojiBean){
                        content = ((EmojiBean)o).emoji;
                    } else if(o instanceof EmoticonEntity){
                        content = ((EmoticonEntity)o).getContent();
                    }

                    if(TextUtils.isEmpty(content)){
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(EmoticonsKeyboardUtils.isFullScreen(this)){
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }
    /// 发送按钮
    private void OnSendBtnClick(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            CLMessageBean messageBean = CLMessageManager.getInstance().sendTextMessage(msg, mUserId, mIsGroup);
            sendMessage(messageBean);
        }
    }
    /// 发送gif图片
    private void OnSendImage(String image) {
        if (!TextUtils.isEmpty(image)) {
            String[] split = image.split("/");
            /// 分割路径,取出图片最好一部分
            String path = split[split.length - 2] + "/" + split[split.length - 1];
            CLMessageBean messageBean = CLMessageManager.getInstance().sendEmojiMessage(path, mUserId, mIsGroup);
            sendMessage(messageBean);
        }
    }

    private void scrollToBottom() {
        mRvSessionView.requestLayout();
        mRvSessionView.post(new Runnable() {
            @Override
            public void run() {
                if (messageList.size() > 0){
                    mRvSessionView.scrollToPosition(messageList.size() - 1);
                }
            }
        });
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() { }

    @Override
    protected void onPause() {
        super.onPause();
        ekBar.reset();
    }

    private void initAudioRecord() {
        if (!EasyPermissions.hasPermissions(this,Manifest.permission.RECORD_AUDIO)){
            EasyPermissions.requestPermissions(this,"需要麦克风权限",0,Manifest.permission.RECORD_AUDIO);
        }
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
                mRecordWindow.showAtLocation(ekBar, 17, 0, 0);
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
                    Toast.makeText(CLSessionActivity.this,"录音文件保存失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                //发送文件
                File file = new File(audioPath.getPath());
                if (file.exists()) {
//                    mPresenter.sendAudioFile(audioPath, duration);
                    Toast.makeText(CLSessionActivity.this,"录制完成 时长 = " + duration,Toast.LENGTH_SHORT).show();
                }
                CLMessageBean messageBean = CLMessageManager.getInstance().sendAudioMessage(audioPath.getPath(), duration, mUserId, mIsGroup,true);
                sendMessage(messageBean);
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
        mRvSessionView = (RecyclerView)findViewById( R.id.rv_sessionView );
        titleBar = (CommonTitleBar) findViewById(R.id.titlebar);
        ekBar = (XhsEmoticonsKeyBoard)findViewById(R.id.ek_bar);

    }

    public void initListener() {

        mRvSessionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /// 滚动隐藏键盘
                ekBar.reset();
            }
        });

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSendBtnClick(ekBar.getEtChat().getText().toString());
                ekBar.getEtChat().setText("");
            }
        });
        /// 监听键盘弹出或者隐藏
        ekBar.setKeyBoardShowOrHiddenListener(new XhsEmoticonsKeyBoard.EmoticonsKeyBoardShowOrHiddenListener() {
            @Override
            public void onKeyBoardIsShow(Boolean isShow) {
                if (isShow){ //键盘弹出,更改recyclerView
                    scrollToBottom();
                }
            }
        });

        // 录音事件
        ekBar.getBtnVoice().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (EasyPermissions.hasPermissions(CLSessionActivity.this,Manifest.permission.RECORD_AUDIO)){
                            AudioRecordManager.getInstance(CLSessionActivity.this).startRecord();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!EasyPermissions.hasPermissions(CLSessionActivity.this,Manifest.permission.RECORD_AUDIO)){
                            return false;
                        }
                        if (isCancelled(view, motionEvent)) {
                            AudioRecordManager.getInstance(CLSessionActivity.this).willCancelRecord();
                        } else {
                            AudioRecordManager.getInstance(CLSessionActivity.this).continueRecord();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!EasyPermissions.hasPermissions(CLSessionActivity.this,Manifest.permission.RECORD_AUDIO)){
                            return false;
                        }
                        AudioRecordManager.getInstance(CLSessionActivity.this).stopRecord();
                        AudioRecordManager.getInstance(CLSessionActivity.this).destroyRecord();
                        break;
                }
                return false;
            }
        });

        mKeyboardMoreView.setMoreItemClickOnListener(new CLKeyBoardMoreGridView.CLMoreItemClickOnListener() {
            @Override
            public void didItem(int postion) {
                switch (postion){
                    case 0://相册
                        gotoAlbum();
                        break;
                    case 1://拍摄
                        Toast.makeText(CLSessionActivity.this,"相机",Toast.LENGTH_SHORT).show();
                        break;
                    case 2://位置

                        break;
                    case 3://红包

                        break;
                }
            }
        });
    }

    /// 相册
    private void gotoAlbum(){
        Intent intent = new Intent(CLSessionActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER);
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
                    Log.e("CSDN_LQR_TYPE", imageItem.mimeType);
                    String imagePath = "";
                    if (isOrig != true){ //压缩
                        imagePath = CLFileUtils.getCachePath() + imageItem.name;
                        Bitmap bitmapFile = BitmapUtils.getSDCardBitmapFile(imageItem.path);
                        BitmapUtils.saveBitmapToSDCardJPEG(bitmapFile,imagePath,80);
                    }else {
                        imagePath = imageItem.path;
                    }
                    CLMessageBean messageBean = CLMessageManager.getInstance().sendImageMessage(imageItem.path,imageItem.name,imageItem.width,imageItem.height,mUserId,mIsGroup,true);
                    sendMessage(messageBean);
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

    private void sendMessage(CLMessageBean messageBean){
        if (messageList.contains(messageBean)){
            return;
        }
        CLMessageBean timeMessage = CLMessageManager.getInstance().sendTimeMessage(mUserId);
        if (timeMessage != null){
            messageList.add(timeMessage);
        }
        messageList.add(messageBean);
        /// 插入并刷新
        adapter.notifyItemInserted(messageList.size());
        /// 滚到最后一个位置
        mRvSessionView.scrollToPosition(messageList.size() - 1);

    }

    // 监听新消息的到来
    private void obseverReceiveMessage(){
        /// 新消息回调
        CLMessageManager.getInstance().setReceiveMessageOnListener(new CLMessageManager.CLReceiveMessageOnListener() {
            @Override
            public void onMessage(final CLMessageBean message) {
                String targetUserId = message.getTargetId();
                /// 判断收到的消息是否是发给当前会话的
                if (!targetUserId.equals(mUserId)){
                    return;
                }

                /// 更新状态
                CLMessageBean.updateRecviveSingleMessageStatus(mUserId,message.getMessageId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(message);
                    }
                });
            }
        });

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

        /// 监听上传状态
        CLMessageManager.getInstance().setUploadStatusOnListener(new CLMessageManager.CLUploadStatusOnListener() {
            @Override
            public void onSuccess(String fileName) {
                uploadFinishUpdateMessage(fileName,CLUploadStatus.UploadStatus_success);
            }
            @Override
            public void onFailure(String fileName) {
                uploadFinishUpdateMessage(fileName,CLUploadStatus.UploadStatus_fail);
            }
        });
    }

    /// 发送成功后更新状态
    private void updateMessageStatus(CLMessageBean message){
        for (int i = messageList.size(); i > 0; i--) {
            CLMessageBean bean = messageList.get(i - 1);
            if (bean.getMessageId().equals(message.getMessageId())){
                bean.setSendStatus(message.getSendStatus());
                bean.setUploadStatus(message.getUploadStatus());
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemChanged(finalI - 1);
                    }
                });
                break;
            }
        }
    }

    private void uploadFinishUpdateMessage(String fileName,CLUploadStatus status){
        for (int i = messageList.size(); i > 0; i--){
            final CLMessageBean bean = messageList.get(i - 1);
            if (bean.getMediaUrl().equals(fileName)) {
                bean.setUploadStatus(status.getTypeName());
                if (status == CLUploadStatus.UploadStatus_success){
                    bean.setSendStatus(CLSendStatus.SendStatus_SEND.getTypeName());
                    CLMessageManager.getInstance().sendMessage(bean,mUserId);
                }else {
                    bean.setSendStatus(CLSendStatus.SendStatus_FAILED.getTypeName());
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(finalI - 1);
                        }
                    });
                }
                break;
            }
        }
    }

}
