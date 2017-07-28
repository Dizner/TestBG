package com.dlrj.yuanqu.view.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.liuguangqiang.swipeback.SwipeBackActivity;

/**
 * Created by Dizner on 2017/7/28.
 * Activity 基类
 */

public class BaseActivity extends SwipeBackActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
    }

}
