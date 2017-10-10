package network.tutoria.com.networkdemo;

import java.lang.reflect.Type;

import network.tutoria.com.networkdemo.bean.GanHuoResult;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.api.CustomParser;
import network.tutoria.com.networkdemo.network.api.RequestBase;

/**
 * Created on 2017/10/10 10:20.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class DemoRequest extends RequestBase<GanHuoResult> {

    private String email, password;

    public DemoRequest(String email, String password) {
        this.email = email;
        this.password = password;
        //设置自定义解析
        setCustomParser(new CustomParser<GanHuoResult>() {

            @Override
            public GanHuoResult parseResult(Type type, String result) {
                return null;
            }
        });
    }

    @Override
    public RequestBuilder getRequestBuilder() {
        return RequestBuilder.get("http://gank.io/api/data/休息视频/1/1").addParam("email", email).addParam("password", password);
    }

    //返回值的类型
    public Type getResultType() {
        return GanHuoResult.class;
    }

}
