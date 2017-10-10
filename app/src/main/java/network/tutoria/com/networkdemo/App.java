package network.tutoria.com.networkdemo;

import android.app.Application;

import network.tutoria.com.networkdemo.network.retrofit.NetworkRequestRetrofitProcessor;

/**
 * Created on 2017/9/28 17:15.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkRequestRetrofitProcessor.init(this);
    }
}
