package com.dlrj.yuanqu.controller.controlinterface;

import com.dlrj.yuanqu.model.modelinterface.IModeiImp;
import com.dlrj.yuanqu.model.modelinterface.IModel;
import com.dlrj.yuanqu.view.viewinterface.IViewInterface;

/**
 * Created by Dizner on 2017/7/28.
 * Controller层接口实现
 */

public class IControllerImp<V> implements IController {

    private IModel model;
    private IViewInterface viewInterface;

    private IControllerImp(IViewInterface viewInterface) {
        this.model = new IModeiImp<V>(this);
        this.viewInterface = viewInterface;
    }
    public static IControllerImp getInstance(IViewInterface viewInterface) {
        return new IControllerImp<>(viewInterface);
    }

    @Override
    public void onRequest(String requestCode,String url,Object argBean) {
        model.onRequest(requestCode,url,argBean);
    }

    @Override
    public void onResponse(String requestCode, int resultCode, String result) {
        viewInterface.onResponse(requestCode,resultCode,result);
    }

}
