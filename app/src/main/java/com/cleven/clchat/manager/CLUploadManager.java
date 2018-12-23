package com.cleven.clchat.manager;

import android.os.Looper;
import android.widget.Toast;

import com.cleven.clchat.app.CLApplication;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLFileUtils;
import com.cleven.clchat.utils.CLUtils;
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

import dev.utils.LogPrintUtils;
import dev.utils.app.assist.manager.ThreadManager;

/**
 * Created by cleven on 2018/12/23.
 */

public class CLUploadManager {
    private static CLUploadManager instance = new CLUploadManager();
    private Bucket bucket;
    private boolean isCancelled;
    private UploadManager manager;

    private CLUploadManager(){}
    public static CLUploadManager getInstance(){
        return instance;
    }


    public void init(){
        EnvContext evn = new EnvContext(CLAPPConst.QINGCLOUD_ACCESS_KEY_ID, CLAPPConst.QINGCLOUD_SECRCT_ACCESS_KEY_ID);
        bucket = new Bucket(evn,"sh1a","cleven-chat-shanghai");
    }

    public void uploadAudio(final String filePath){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 上传音频
        upload("audios/",file);
    }

    public void uploadVideo(final String filePath){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 上传视频
        upload("videos/",file);
    }

    public void uploadAvatar(final String filePath){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 上传头像
        upload("avatars/",file);
    }

    public void uploadImage(final String filePath){
        final File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            LogPrintUtils.eTag("青云", "文件不存在");
            return;
        }
        /// 上传图片
        upload("images/",file);
    }


    private void upload(final String objectKey, final File file){

        FileRecorder recorder = null;
        try {
            recorder = new FileRecorder(CLFileUtils.getDownloadDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CancellationHandler cancellationHandler = new CancellationHandler() {
            @Override
            public boolean isCancelled() {
                return isCancelled;
            }
        };

        BodyProgressListener listener = new BodyProgressListener() {
            @Override
            public void onProgress(final long len, final long size) {
                int progress = (int) ((len * 100) / size);
                LogPrintUtils.eTag("青云","progress =" + progress);
                Toast.makeText(CLApplication.mContext,"progress = " + progress,Toast.LENGTH_SHORT).show();
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

                if (output.getStatueCode() == 200 || output.getStatueCode() == 201) {
                    LogPrintUtils.eTag(TAG, "Upload success.");
                    Toast.makeText(CLApplication.mContext," 上传成功",Toast.LENGTH_SHORT).show();
                } else if (output.getStatueCode() == QSConstant.REQUEST_ERROR_CANCELLED) {
                    LogPrintUtils.eTag(TAG, "Stopped.");
                } else {
                    LogPrintUtils.eTag(TAG, "Error: " + output.getMessage());
                    Toast.makeText(CLApplication.mContext,"上传失败",Toast.LENGTH_SHORT).show();
                }
            }
        };

        manager = new UploadManager(bucket, recorder, listener, cancellationHandler, callback);
        ThreadManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
//                    manager.put(file);
                    String imageName = CLUtils.timeStamp + ".png";
                    manager.put(file,objectKey + imageName, imageName, "");
                    Looper.loop();
                } catch (QSException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
