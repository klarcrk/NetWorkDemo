package network.tutoria.com.networkdemo.network.api;

import java.io.File;

/**
 * Created on 2017/9/28 15:27.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public interface NetworkResultInterface<T> {

    void onError(Throwable error);

    //接口数据加载完成 返回string
    void onLoadSuccess(T result);

    void onGetUploadProgress(int progress);
    //下载文件进度更新
    void onGetDownloadProgress(int progress);

    //下载文件完成
    void onDownloadSuccess(File downloadFile);

}
