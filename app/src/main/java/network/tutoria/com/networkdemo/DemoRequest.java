package network.tutoria.com.networkdemo;

import java.lang.reflect.Type;
import java.util.HashMap;

import network.tutoria.com.networkdemo.bean.LoginBean;
import network.tutoria.com.networkdemo.bean.RegisterBean;
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
        //设置自定义解析
    }

    //返回值的类型
    public void doRegister(NetworkResultHandler<RegisterBean> networkResultHandler) {
        cancelPreRequest();
        requestBuilder = RequestBuilder.get("http://gank.io/api/data/休息视频/1/1").addParam("regist", email).addParam("password", password).setCustomParser(new CustomParser<RegisterBean>() {
            @Override
            public RegisterBean parseResult(Type type, String result) {
                return null;
            }
        }).doRequest(RegisterBean.class, networkResultHandler);
    }


    public void doLogin(HashMap<String, String> params, NetworkResultHandler<LoginBean> networkResultHandler) {
        cancelPreRequest();
        requestBuilder = RequestBuilder.get("http://gank.io/api/data/休息视频/1/1").addParams(params).doRequest(LoginBean.class, networkResultHandler);
    }

}
