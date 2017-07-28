package com.dlrj.yuanqu.model.modelinterface;

import com.dlrj.yuanqu.controller.controlinterface.IController;
import com.dlrj.yuanqu.controller.utils.HTTP;

/**
 * Created by Dizner on 2017/7/28.
 * model层接口实现
 */

public class IModeiImp<T> implements IModel {

    private IController controller;

    public IModeiImp(IController controller) {
        this.controller = controller;
    }

    @Override
    public void onRequest(final String requestCode, String url, Object argBean) {
        // 此处进行网络请求 异步请求，成功之后回调controller的 onResponse(String str)传回结果json串。

        HTTP.getInstance().getJsonData(argBean, url, new HTTP.HttpRequestCallBack() {
            @Override
            public void onSuccessful(String json) {
                // 注意在此处添加代码判断请求是否成功，否则请求失败会导致View层崩溃
                controller.onResponse(requestCode,HTTP.SUCCESS,json);
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onError(Throwable throwable) {
                controller.onResponse(requestCode,HTTP.FAILURE,null);
            }

            @Override
            public void onStart() {

            }
        });
    }
}
