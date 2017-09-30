package network.tutoria.com.networkdemo.network.api;

import network.tutoria.com.networkdemo.network.RequestBuilder;

/**
 * Created on 2017/9/28 16:39.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public interface NetworkRequestProcessor {

    <T> NetworkRequestProcessor startGetRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler);

    <T> NetworkRequestProcessor startPostRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler);

    <T> NetworkRequestProcessor startUploadRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler);

    <T> NetworkRequestProcessor startDownloadRequest(RequestBuilder requestBuilder, NetworkResultHandler<T> resultHandler);

    NetworkRequestProcessor cancelRequest(RequestBuilder requestBuilder);
}
