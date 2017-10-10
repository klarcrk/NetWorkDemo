package network.tutoria.com.networkdemo.network.api;

import java.lang.reflect.Type;

/**
 * Created on 2017/10/10 13:43.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public interface CustomParser<T> {
    //parseResult在线程中调用，不要修改ui
    T parseResult(Type type, String result);
}
