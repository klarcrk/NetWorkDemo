package network.tutoria.com.networkdemo.network.api;

import network.tutoria.com.networkdemo.network.RequestBuilder;

/**
 * Created on 2017/10/10 13:55.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class HeaderUtil {

    public static void addGlobalHeader(RequestBuilder requestBuilder) {
        requestBuilder.addHeader("head1", "head1");
        requestBuilder.addHeader("head2", "head2");
        requestBuilder.addHeader("head3", "head3");
    }
}
