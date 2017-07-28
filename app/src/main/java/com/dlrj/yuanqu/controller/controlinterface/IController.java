package com.dlrj.yuanqu.controller.controlinterface;

/**
 * Created by Dizner on 2017/7/28.
 * 控制层基础接口
 */

public interface IController {
    void onRequest(String requestCode,String url,Object argBean);
    void onResponse(String requestCode,int resultCode,String result);
}
