package network.tutoria.com.networkdemo.network.util;

import com.google.gson.Gson;

/**
 * Created on 2017/9/29 10:11.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class GsonUtil {
    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}
