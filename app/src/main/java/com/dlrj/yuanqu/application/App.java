package com.dlrj.yuanqu.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.dlrj.yuanqu.controller.constant.ConstantURL;
import com.dlrj.yuanqu.controller.utils.HTTP;
import com.dlrj.yuanqu.controller.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dizner on 2017/7/28.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks{
    private List<Activity> activities;
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化工作
        activities = new ArrayList<>();
        //请求工具初始化
        HTTP.getInstance().init(getApplicationContext(), ConstantURL.Base_Url).setIsDeBug(true).setTAG("HTTP_REQ");
        //SP工具初始化
        SharedPreferencesUtil.getInstance().setContext(this);


    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
