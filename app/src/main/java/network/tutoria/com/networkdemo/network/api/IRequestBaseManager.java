package network.tutoria.com.networkdemo.network.api;

/**
 * Created on 2017/10/13 11:11.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */
/*
用于管理RequestBase
现在实现这个接口的类有BaseActivity,BaseFragment
BaseActivity,BaseFragment会在销毁时自动取消所有保存的RequestBase里的请求
 */
public interface IRequestBaseManager {
    void addRequestBase(RequestBase requestBase);

    void removeRequestBase(RequestBase requestBase);
}
