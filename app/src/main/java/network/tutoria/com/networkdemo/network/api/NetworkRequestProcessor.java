package network.tutoria.com.networkdemo.network.api;

import java.lang.reflect.Type;

import network.tutoria.com.networkdemo.network.RequestBuilder;

/**
 * Created on 2017/9/28 16:39.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public interface NetworkRequestProcessor {

    <T> void startGetRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler, Type type);

    <T> void startPostRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler, Type type);

    <T> void startUploadRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler, Type type);

    <T> void startDownloadRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler);

    void cancelRequest(RequestBuilder requestBuilder);
}
