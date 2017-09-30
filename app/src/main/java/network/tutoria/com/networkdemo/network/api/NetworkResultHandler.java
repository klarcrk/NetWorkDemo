package network.tutoria.com.networkdemo.network.api;

import java.io.File;

/**
 * Created on 2017/9/28 15:27.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public abstract class NetworkResultHandler<T> implements NetworkResultInterface<T> {

    @Override
    public void onError(Throwable error) {

    }

    @Override
    public void onLoadSuccess(T result) {

    }

    @Override
    public void onGetUploadProgress(int progress) {

    }

    @Override
    public void onGetDownloadProgress(int progress) {

    }

    @Override
    public void onDownloadSuccess(File downloadFile) {

    }
}
