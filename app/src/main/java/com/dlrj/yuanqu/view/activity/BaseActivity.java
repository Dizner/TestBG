package com.dlrj.yuanqu.view.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.dlrj.yuanqu.R;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

/**
 * Created by Dizner on 2017/7/28.
 * Activity 基类
 */

public class BaseActivity extends SwipeBackActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        setTheme(R.style.Base_Activity_Theme);
        setContentView(R.layout.activity_base);
//        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
    }

}
