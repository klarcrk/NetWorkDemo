package network.tutoria.com.networkdemo.network.api;

import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.retrofit.NetworkRequestRetrofitProcessor;

/**
 * Created on 2017/10/10 10:15.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public abstract class RequestBase {

    //子类实现创建请求的方法
    private boolean cancelPreRequest = true;
    //自定义方法解析请求结果
    protected RequestBuilder requestBuilder;

    public void cancelPreRequest() {
        if (cancelPreRequest) {
            cancelRequest();
        }
    }

    /*
     *取消上次发出的请求
     */
    public void cancelRequest() {
        if (requestBuilder != null) {
            NetworkRequestProcessor requestProcessor = NetworkRequestRetrofitProcessor.getInstance();
            requestProcessor.cancelRequest(requestBuilder);
        }
    }
}
