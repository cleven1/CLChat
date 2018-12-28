package com.cleven.clchat.manager;

import android.os.Looper;

import com.cleven.clchat.app.CLApplication;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLFileUtils;
import com.qingstor.sdk.config.EnvContext;
import com.qingstor.sdk.constants.QSConstant;
import com.qingstor.sdk.exception.QSException;
import com.qingstor.sdk.model.OutputModel;
import com.qingstor.sdk.request.BodyProgressListener;
import com.qingstor.sdk.request.CancellationHandler;
import com.qingstor.sdk.service.Bucket;
import com.qingstor.sdk.upload.UploadManager;
import com.qingstor.sdk.upload.UploadManagerCallback;
import com.qingstor.sdk.upload.impl.FileRecorder;

import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import dev.utils.LogPrintUtils;
import dev.utils.app.assist.manager.ThreadManager;

/**
 * Created by cleven on 2018/12/23.
 */

public class CLUploadManager {
    private static CLUploadManager instance = new CLUploadManager();
    private static Bucket bucket;
    private static boolean isCancelled;

    public CLUploadManager(){}
    public static CLUploadManager getInstance(){
        return instance;
    }
    /// 上传回调接口
    private CLUploadOnLitenser uploadOnLitenser;
    public void setUploadOnLitenser(CLUploadOnLitenser uploadOnLitenser) {
        this.uploadOnLitenser = uploadOnLitenser;
    }
    public interface CLUploadOnLitenser {
        void uploadSuccess(String fileName);
        void uploadError(String fileName);
        void uploadProgress(int progress);
        void uploadCancel(String fileName);
    }


    public void init(){
        EnvContext evn = new EnvContext(CLAPPConst.QINGCLOUD_ACCESS_KEY_ID, CLAPPConst.QINGCLOUD_SECRCT_ACCESS_KEY_ID);
        bucket = new Bucket(evn,"sh1a","cleven-chat-shanghai");
    }

    public void uploadAudio(final String filePath,String fileName,CLUploadOnLitenser litenser){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 转发音频格式
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                /// 上传音频
                upload("audios/",convertedFile,fileName,litenser);
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
            }
        };
        AndroidAudioConverter.with(CLApplication.mContext)
                // Your current audio file
                .setFile(file)
                // Your desired audio format
                .setFormat(AudioFormat.MP3)
                // An callback to know when conversion is finished
                .setCallback(callback)
                // Start conversion
                .convert();
    }

    public void uploadVideo(final String filePath,String fileName,CLUploadOnLitenser litenser){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 上传视频
        upload("videos/",file,fileName,litenser);
    }

    public void uploadAvatar(final String filePath,String fileName,CLUploadOnLitenser litenser){
        final File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 上传头像
        upload("avatars/",file,fileName,litenser);
    }

    public void uploadImage(final String filePath,String fileName,CLUploadOnLitenser litenser){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        init();
        /// 上传图片
        upload("images/",file,fileName,litenser);
    }


    private void upload(final String objectKey, final File file, final String fileName, final CLUploadOnLitenser litenser){

        FileRecorder recorder = null;
        try {
            recorder = new FileRecorder(CLFileUtils.getDownloadDir());
        } catch (IOException e) {
            e.printStackTrace();
            if (litenser != null){
                litenser.uploadError(fileName);
            }
            return;
        }

        CancellationHandler cancellationHandler = new CancellationHandler() {
            @Override
            public boolean isCancelled() {
                if (litenser != null){
                    litenser.uploadCancel(fileName);
                }
                return isCancelled;
            }
        };

        BodyProgressListener listener = new BodyProgressListener() {
            @Override
            public void onProgress(final long len, final long size) {
                int progress = (int) ((len * 100) / size);
                LogPrintUtils.eTag("青云","progress =" + progress);
                if (litenser != null){
                    litenser.uploadProgress(progress);
                }
            }
        };

        UploadManagerCallback callback = new UploadManagerCallback() {
            final String TAG = "UploadManagerCallback";

            @Override
            public void onAPIResponse(OutputModel output) throws QSException {
                // 上传成功或失败会回调此方法
                LogPrintUtils.eTag(TAG, "code = " + output.getCode());
                LogPrintUtils.eTag(TAG, "statueCode = " + output.getStatueCode());
                LogPrintUtils.eTag(TAG, "message = " + output.getMessage());
                LogPrintUtils.eTag(TAG, "request ID = " + output.getRequestId());
                LogPrintUtils.eTag(TAG, "url = " + output.getUrl());
                LogPrintUtils.eTag(TAG, "fileName = " + fileName);

                if (output.getStatueCode() == 200 || output.getStatueCode() == 201) {
                    LogPrintUtils.eTag(TAG, "Upload success.");
                    if (litenser != null){
                        litenser.uploadSuccess(fileName);
                    }
                } else if (output.getStatueCode() == QSConstant.REQUEST_ERROR_CANCELLED) {
                    LogPrintUtils.eTag(TAG, "Stopped.");
                    if (litenser != null){
                        litenser.uploadCancel(fileName);
                    }
                } else {
                    LogPrintUtils.eTag(TAG, "Error: " + output.getMessage());
                    if (litenser != null){
                        litenser.uploadError(fileName);
                    }
                }
            }
        };

        final UploadManager manager = new UploadManager(bucket, recorder, listener, cancellationHandler, callback);
        ThreadManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    manager.put(file,objectKey + fileName, fileName, "");
                    Looper.loop();
                } catch (QSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
