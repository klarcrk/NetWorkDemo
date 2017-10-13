package network.tutoria.com.networkdemo.base;


import android.app.Fragment;

import java.util.HashSet;

import network.tutoria.com.networkdemo.network.api.IRequestBaseManager;
import network.tutoria.com.networkdemo.network.api.RequestBase;

/**
 * Created on 2017/10/13 11:13.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class BaseFragment extends Fragment implements IRequestBaseManager {

    private HashSet<RequestBase> cachedRequestBased = new HashSet<>();

    @Override
    public void addRequestBase(RequestBase requestBase) {
        cachedRequestBased.add(requestBase);
    }

    @Override
    public void removeRequestBase(RequestBase requestBase) {
        cachedRequestBased.remove(requestBase);
    }

    @Override
    public void onDestroy() {
        for (RequestBase requestBase : cachedRequestBased) {
            requestBase.cancelAllRequest();
        }
        cachedRequestBased.clear();
        super.onDestroy();
    }

}
