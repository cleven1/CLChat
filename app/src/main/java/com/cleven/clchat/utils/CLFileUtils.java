package com.cleven.clchat.utils;

import android.os.Environment;

import com.cleven.clchat.app.CLApplication;

import java.io.File;

import dev.utils.app.AppUtils;

/**
 * @创建者 cleven
 * @描述 写文件的工具类
 */
public class CLFileUtils {

    public static final String ROOT_DIR = "Android/data/"
            + AppUtils.getAppPackageName();
    public static final String DOWNLOAD_DIR = "download";
    public static final String CACHE_DIR = "cache";
    public static final String ICON_DIR = "icon";

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取下载目录
     */
    public static String getDownloadDir() {
        return getDir(DOWNLOAD_DIR);
    }

    /**
     * 获取缓存目录
     */
    public static String getCacheDir() {
        return getDir(CACHE_DIR);
    }

    /**
     * 获取icon目录
     */
    public static String getIconDir() {
        return getDir(ICON_DIR);
    }

    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    public static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 获取SD下的应用目录
     */
    public static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 获取应用的cache目录
     */
    public static String getCachePath() {
        File f = CLApplication.mContext.getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

}
