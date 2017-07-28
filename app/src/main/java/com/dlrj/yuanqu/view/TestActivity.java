package com.dlrj.yuanqu.view;

import android.os.Bundle;

import com.dlrj.yuanqu.R;
import com.dlrj.yuanqu.view.activity.BaseActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
    }
}
