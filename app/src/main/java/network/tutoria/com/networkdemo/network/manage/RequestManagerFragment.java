package network.tutoria.com.networkdemo.network.manage;

import android.app.Fragment;
import android.util.Log;

import java.util.HashSet;

/**
 * Created on 2017/10/13 14:43.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class RequestManagerFragment extends Fragment {

    private HashSet<RequestBase> cachedRequestBased = new HashSet<>();

    public void addRequestBase(RequestBase requestBase) {
        cachedRequestBased.add(requestBase);
    }

    public void removeRequestBase(RequestBase requestBase) {
        cachedRequestBased.remove(requestBase);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("tag", "fragment也被onStop了");
    }

    @Override
    public void onDestroy() {
        for (RequestBase requestBase : cachedRequestBased) {
            requestBase.cancelAllRequest();
        }
        cachedRequestBased.clear();
        Log.i("tag", "fragment也被干掉了");
        super.onDestroy();
    }
}
