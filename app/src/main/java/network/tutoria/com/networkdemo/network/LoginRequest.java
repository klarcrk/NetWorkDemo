package network.tutoria.com.networkdemo.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/10/9 13:56.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class LoginRequest {

    public static RequestBuilder getLoginBuilder() {
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        return RequestBuilder.get("rrrxx").addParams(params).addHeaders(headers);
    }
}
