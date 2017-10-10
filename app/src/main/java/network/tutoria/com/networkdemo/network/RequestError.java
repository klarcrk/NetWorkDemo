package network.tutoria.com.networkdemo.network;

/**
 * Created on 2017/10/10 15:19.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class RequestError extends Exception {

    //错误信息的对象
    private Throwable error;
    //可能有结果返回，但是出错了
    private String requestResult;

    public Throwable getError() {
        return error;
    }

    public RequestError setError(Throwable error) {
        this.error = error;
        return this;
    }

    public String getRequestResult() {
        return requestResult;
    }

    public RequestError setRequestResult(String requestResult) {
        this.requestResult = requestResult;
        return this;
    }

    @Override
    public void printStackTrace() {
        if (error != null) {
            error.printStackTrace();
        }
    }
}
