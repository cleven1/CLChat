package com.cleven.clchat.home.CLEmojiCommon.userDef;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.cleven.clchat.R;

import sj.keyboard.XhsEmoticonsKeyBoard;
import sj.keyboard.utils.EmoticonsKeyboardUtils;

public class CLCustomEmoticonsKeyBoard extends XhsEmoticonsKeyBoard {

    public int APPS_HEIGHT = 240;

    public CLCustomEmoticonsKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void inflateKeyboardBar(){
        mInflater.inflate(R.layout.view_keyboard_userdef, this);
    }

    @Override
    protected View inflateFunc(){
        return mInflater.inflate(R.layout.view_func_emoticon_userdef, null);
    }

    @Override
    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
        mLyKvml.hideAllFuncView();
        mBtnFace.setImageResource(R.mipmap.ic_cheat_emo);
    }

    @Override
    public void onFuncChange(int key) {
        if (FUNC_TYPE_EMOTION == key) {
            mBtnFace.setImageResource(R.mipmap.ic_cheat_keyboard);
        } else {
            mBtnFace.setImageResource(R.mipmap.ic_cheat_emo);
        }
        checkVoice();
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        if (mLyKvml.getCurrentFuncKey() == FUNC_TYPE_APPPS) {
            setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
        }
    }

    @Override
    protected void showText() {
        mEtChat.setVisibility(VISIBLE);
        mBtnFace.setVisibility(VISIBLE);
        mBtnVoice.setVisibility(GONE);
    }

    @Override
    protected void showVoice() {
        mEtChat.setVisibility(GONE);
        mBtnFace.setVisibility(GONE);
        mBtnVoice.setVisibility(VISIBLE);
        reset();
    }

    @Override
    protected void checkVoice() {
        if (mBtnVoice.isShown()) {
            mBtnVoiceOrText.setImageResource(R.mipmap.ic_cheat_keyboard);
        } else {
            mBtnVoiceOrText.setImageResource(R.mipmap.ic_cheat_voice);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == com.keyboard.view.R.id.btn_voice_or_text) {
            if (mEtChat.isShown()) {
                mBtnVoiceOrText.setImageResource(R.mipmap.ic_cheat_keyboard);
                showVoice();
            } else {
                showText();
                mBtnVoiceOrText.setImageResource(R.mipmap.ic_cheat_voice);
                EmoticonsKeyboardUtils.openSoftKeyboard(mEtChat);
            }
        } else if (i == com.keyboard.view.R.id.btn_face) {
            toggleFuncView(FUNC_TYPE_EMOTION);
        } else if (i == com.keyboard.view.R.id.btn_multimedia) {
            toggleFuncView(FUNC_TYPE_APPPS);
//            setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
        }
    }
}
