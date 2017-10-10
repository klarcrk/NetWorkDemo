package network.tutoria.com.networkdemo;

import java.lang.reflect.Type;
import java.util.HashMap;

import network.tutoria.com.networkdemo.bean.LoginBean;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.api.CustomParser;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;
import network.tutoria.com.networkdemo.network.api.RequestBase;

/**
 * Created on 2017/10/10 10:20.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class DemoRequest extends RequestBase {

    private String email, password;

    public DemoRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void doRegister(NetworkResultHandler<Object> networkResultHandler) {
        cancelPreRequest();
        RequestBuilder requestBuilder = RequestBuilder.get("http://gank.io/api/data/休息视频/1/1").addParam("regist", email).addParam("password", password).setCustomParser(new CustomParser() {
            @Override
            public Object parseResult(Type type, String result) {
                return null;
            }
        }).doRequest(LoginBean.class, networkResultHandler);
    }


    public void doLogin(HashMap<String, String> params, NetworkResultHandler<LoginBean> networkResultHandler) {
        cancelPreRequest();
        requestBuilder = RequestBuilder.post("http://gank.io/api/data/休息视频/1/1").addParams(params)
                .setCustomParser(new CustomParser<LoginBean>() {
                    @Override
                    public LoginBean parseResult(Type type, String result) {
                        //自定义解析结果
                        return null;
                    }
                })
                .doRequest(LoginBean.class, networkResultHandler);
    }

}
