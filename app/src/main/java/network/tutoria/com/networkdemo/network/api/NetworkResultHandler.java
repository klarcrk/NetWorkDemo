package network.tutoria.com.networkdemo.network.api;

import java.io.File;

import network.tutoria.com.networkdemo.network.RequestError;

/**
 * Created on 2017/9/28 15:27.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public abstract class NetworkResultHandler<T> {

    public void onError(RequestError error) {
    }

    //接口数据加载完成
    public void onLoadSuccess(T result) {
    }

    public void onGetUploadProgress(int progress) {

    }

    //下载文件进度更新
    public void onGetDownloadProgress(int progress) {

    }

    //下载文件完成
    public void onDownloadSuccess(File downloadFile) {

    }
}
