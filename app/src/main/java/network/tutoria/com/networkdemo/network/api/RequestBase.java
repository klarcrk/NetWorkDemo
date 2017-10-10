package network.tutoria.com.networkdemo.network.api;

import java.lang.reflect.Type;

import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.retorfit.NetworkRequestRetrofitProcessor;

/**
 * Created on 2017/10/10 10:15.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public abstract class RequestBase<T> {

    public RequestBase setResultHandler(NetworkResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    protected NetworkResultHandler<T> resultHandler;

    //子类实现创建请求的方法
    public abstract RequestBuilder getRequestBuilder();

    public abstract Type getResultType();

    private boolean cancelPreRequest = true;

    public RequestBase<T> setCancelPreRequest(boolean cancelPreRequest) {
        this.cancelPreRequest = cancelPreRequest;
        return this;
    }

    public RequestBase<T> setCustomParser(CustomParser customParser) {
        this.customParser = customParser;
        return this;
    }

    //自定义方法解析请求结果
    private CustomParser customParser;

    private RequestBuilder requestBuilder;

    public void execute() {
        if (cancelPreRequest) {
            cancelRequest();
        }
        requestBuilder = getRequestBuilder().setCustomParser(customParser);
        requestBuilder.execute(getResultType(), resultHandler);
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
