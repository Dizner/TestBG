package com.dlrj.yuanqu.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dlrj.yuanqu.R;
import com.dlrj.yuanqu.controller.constant.ConstantURL;
import com.dlrj.yuanqu.controller.utils.HTTP;
import com.dlrj.yuanqu.view.TestActivity;
import com.dlrj.yuanqu.view.fragment.HomeFragment;
import com.dlrj.yuanqu.view.viewinterface.IViewInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements IViewInterface {

    @BindView(R.id.fl_main_layout)
    FrameLayout flMainLayout;
    @BindView(R.id.iv_main_img_tab1)
    ImageView ivMainImgTab1;
    @BindView(R.id.tv_main_txt_tab1)
    TextView tvMainTxtTab1;
    @BindView(R.id.ll_main_btn_tab1)
    RelativeLayout llMainBtnTab1;
    @BindView(R.id.iv_main_img_tab2)
    ImageView ivMainImgTab2;
    @BindView(R.id.tv_main_txt_tab2)
    TextView tvMainTxtTab2;
    @BindView(R.id.ll_main_btn_tab2)
    RelativeLayout llMainBtnTab2;
    @BindView(R.id.iv_main_img_tab3)
    ImageView ivMainImgTab3;
    @BindView(R.id.tv_main_txt_tab3)
    TextView tvMainTxtTab3;
    @BindView(R.id.ll_main_btn_tab3)
    RelativeLayout llMainBtnTab3;
    @BindView(R.id.iv_main_img_tab4)
    ImageView ivMainImgTab4;
    @BindView(R.id.tv_main_txt_tab4)
    TextView tvMainTxtTab4;
    @BindView(R.id.ll_main_btn_tab4)
    RelativeLayout llMainBtnTab4;
    @BindView(R.id.ll_main_tabs)
    LinearLayout llMainTabs;


    private FragmentManager fragmentManager;
    private int currentTabIndex = 0;
    private FragmentTransaction transaction;
    private List<Fragment> mainPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        initView();

//        TestBean args = new TestBean();
//        args.setItemId("1003");
//        args.setUserId("91");
//        IControllerImp.getInstance(this).onRequest(ConstantURL.Test_Url,ConstantURL.Test_Url,null);
    }

    private void initView() {

    }

    private void init() {

        mainPages = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mainPages.add(new HomeFragment());
        }
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fl_main_layout,mainPages.get(0)).commit();
//        switchFragment(0);
        switchTabs(0);
    }

    @Override
    public void onResponse(String requestCode, int resultCode, String result) {
        if (resultCode == HTTP.SUCCESS) {
            switch (requestCode) {
                case ConstantURL.Test_Url:
                    Log.d("TRTTT", result);
                    break;
            }
        }
    }

    @OnClick({R.id.ll_main_btn_tab1, R.id.ll_main_btn_tab2, R.id.ll_main_btn_tab3, R.id.ll_main_btn_tab4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_main_btn_tab1:
                switchFragment(0);
                break;
            case R.id.ll_main_btn_tab2:
                switchFragment(1);
                break;
            case R.id.ll_main_btn_tab3:
                switchFragment(2);
                break;
            case R.id.ll_main_btn_tab4:
                skipActivity(TestActivity.class);
//                startActivity(new Intent(this, TestActivity.class));
//                switchFragment(3);
                break;
        }
    }

    private void switchTabs(int i) {
        ivMainImgTab1.setImageResource(R.mipmap.ic_launcher);
        tvMainTxtTab1.setTextColor(getResources().getColor(R.color.gray_5));
        ivMainImgTab2.setImageResource(R.mipmap.ic_launcher);
        tvMainTxtTab2.setTextColor(getResources().getColor(R.color.gray_5));
        ivMainImgTab3.setImageResource(R.mipmap.ic_launcher);
        tvMainTxtTab3.setTextColor(getResources().getColor(R.color.gray_5));
        ivMainImgTab4.setImageResource(R.mipmap.ic_launcher);
        tvMainTxtTab4.setTextColor(getResources().getColor(R.color.gray_5));


        switch (i) {
            case 0:
                ivMainImgTab1.setImageResource(R.mipmap.tab_1_select);
                tvMainTxtTab1.setTextColor(getResources().getColor(R.color.red));
                break;
            case 1:
                ivMainImgTab2.setImageResource(R.mipmap.tab_1_select);
                tvMainTxtTab2.setTextColor(getResources().getColor(R.color.red));
                break;
            case 2:
                ivMainImgTab3.setImageResource(R.mipmap.tab_1_select);
                tvMainTxtTab3.setTextColor(getResources().getColor(R.color.red));
                break;
            case 3:
                ivMainImgTab4.setImageResource(R.mipmap.tab_1_select);
                tvMainTxtTab4.setTextColor(getResources().getColor(R.color.red));
                break;
        }


    }


    private void switchFragment(int tabindex) {
        switchTabs(tabindex);
        if (currentTabIndex != tabindex) {
            transaction = fragmentManager.beginTransaction();
            // 定义当前碎片与即将加载的碎片
            Fragment fromFragment = mainPages.get(currentTabIndex);
            Fragment toFragment = mainPages.get(tabindex);
            if (!toFragment.isAdded()) {
                // 如果该碎片还没有被添加到事务中，则新添加到事务
                transaction.hide(fromFragment).add(R.id.fl_main_layout,
                        toFragment);
            } else {
                // 如果该碎片已经被添加到事务中，则从事务中取出该碎片进行显示即可。无需销毁再重新创建。
                transaction.hide(fromFragment).show(toFragment);
            }
            // 提交执行过的事务
            transaction.addToBackStack(null);
            transaction.commit();
            currentTabIndex = tabindex;
        }
    }

    private void skipActivity(Class<?> classOf) {
        Intent intent = new Intent(getApplicationContext(), classOf);
        startActivity(intent);
    }

}
