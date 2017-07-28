package com.dlrj.yuanqu.controller.utils;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;

/**
 * Created by Dizner on 2017/7/11.
 * SharedPreferences Key存储类 ，注意设置App在SD卡中的目录名称
 */

public class SP {
    public static final String APP_FILE_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + File.separator + "RApp" + File.separator + "download";
    //APP在SD卡中的目录名称
    private static String PATH_NAME = "RJ_Base";

    public static String DRIVE_IMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String APP_FILE_DOWNLOAD_PATH(Context context) {
        return Environment.getExternalStorageDirectory() + File.separator + PATH_NAME + File.separator + "download";
    }
}
