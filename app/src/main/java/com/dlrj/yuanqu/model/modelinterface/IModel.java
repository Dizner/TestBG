package com.dlrj.yuanqu.model.modelinterface;

/**
 * Created by Dizner on 2017/7/28.
 */

public interface IModel<T> {
    void onRequest(String requestCode,String url,Object argBean);
}
