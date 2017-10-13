package network.tutoria.com.networkdemo.network.manage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.api.NetworkRequestProcessor;
import network.tutoria.com.networkdemo.network.retrofit.NetworkRequestRetrofitProcessor;

/**
 * Created on 2017/10/10 10:15.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public abstract class RequestBase {

    private final String fragmentTagForRequest = "fragmentTagForRequest";

    /*
    *
    */
    public RequestBase(FragmentActivity requestBaseManager) {
        android.support.v4.app.FragmentManager supportFragmentManager = requestBaseManager.getSupportFragmentManager();
        initFragmentV4(supportFragmentManager);
    }

    public RequestBase(Fragment fragment) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        initFragment(childFragmentManager);
    }

    public RequestBase(android.support.v4.app.Fragment fragment) {
        android.support.v4.app.FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        initFragmentV4(childFragmentManager);
    }

    /*
    fragmentManager添加一个无ui fragment
    监听绑定主fragment或者是activity的onDestroy方法
     */

    private void initFragment(FragmentManager fragmentManager) {
        RequestManagerFragment managerFragment = findFragment(fragmentManager);
        if (managerFragment == null) {
            managerFragment = new RequestManagerFragment();
            fragmentManager.beginTransaction().add(managerFragment, fragmentTagForRequest).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        managerFragment.addRequestBase(this);
    }

    private void initFragmentV4(android.support.v4.app.FragmentManager fragmentManager) {
        RequestManagerV4Fragment managerFragment = findV4Fragment(fragmentManager);
        if (managerFragment == null) {
            managerFragment = new RequestManagerV4Fragment();
            fragmentManager.beginTransaction().add(managerFragment, fragmentTagForRequest).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        managerFragment.addRequestBase(this);
    }

    private RequestManagerV4Fragment findV4Fragment(android.support.v4.app.FragmentManager childFragmentManager) {
        return (RequestManagerV4Fragment) childFragmentManager.findFragmentByTag(fragmentTagForRequest);
    }

    private RequestManagerFragment findFragment(FragmentManager childFragmentManager) {
        return (RequestManagerFragment) childFragmentManager.findFragmentByTag(fragmentTagForRequest);
    }




    /*
     *
     *通过url取消并删除之前发起的请求
     *
     */
    public void cancelRequest(@android.support.annotation.NonNull final String url) {
        Flowable.fromIterable(requestBuilders).filter(new Predicate<RequestBuilder>() {
            @Override
            public boolean test(@NonNull RequestBuilder requestBuilder) throws Exception {
                return url.equals(requestBuilder.getUrl());
            }
        }).subscribe(new Consumer<RequestBuilder>() {
            @Override
            public void accept(RequestBuilder requestBuilder) throws Exception {
                cancelRequest(requestBuilder);
                requestBuilders.remove(requestBuilder);
            }
        });
    }

    /*
     *取消上次发出的请求
     */
    public void cancelRequest(RequestBuilder requestBuilder) {
        NetworkRequestProcessor requestProcessor = NetworkRequestRetrofitProcessor.getInstance();
        requestProcessor.cancelRequest(requestBuilder);
    }

    private HashSet<RequestBuilder> requestBuilders = new HashSet<>();

    /*
     *将一个请求添加到管理类中
     */
    protected RequestBuilder addRequestBuilder(RequestBuilder requestBuilder) {
        requestBuilders.add(requestBuilder);
        return requestBuilder;
    }

    protected void removeRequestBuilder(RequestBuilder requestBuilder) {
        requestBuilders.remove(requestBuilder);
    }

    //是不是需要检查删除保存的请求对象 可以自己修改
    public boolean isNeedCheckRequest() {
        return requestBuilders.size() > 2;
    }

    /*
    检查一下 删除已经完成的请求对象
     */
    public void checkRequest() {
        if (!requestBuilders.isEmpty()) {
            Iterator<RequestBuilder> requestBuilderIterator = requestBuilders.iterator();
            while (requestBuilderIterator.hasNext()) {
                RequestBuilder request = requestBuilderIterator.next();
                if (request.isDone()) {
                    //删除已经完成的请求
                    requestBuilderIterator.remove();
                }
            }
        }
    }


    public void cancelAllRequest() {
        if (!requestBuilders.isEmpty()) {
            for (RequestBuilder request : requestBuilders) {
                if (!request.isDone()) {
                    cancelRequest(request);
                }
            }
            requestBuilders.clear();
        }
    }

    protected RequestBuilder requestGet(String url) {
        if (isNeedCheckRequest()) {
            checkRequest();
        }
        return addRequestBuilder(RequestBuilder.get(url));
    }

    protected RequestBuilder requestPost(String url) {
        if (isNeedCheckRequest()) {
            checkRequest();
        }
        return addRequestBuilder(RequestBuilder.post(url));
    }

    protected RequestBuilder requestDownload(String url, @NonNull File downloadTargetFile) {
        if (isNeedCheckRequest()) {
            checkRequest();
        }
        return addRequestBuilder(RequestBuilder.download(url, downloadTargetFile));
    }

    protected RequestBuilder requestUpload(String url, @NonNull HashMap<String, File> filePart) {
        if (isNeedCheckRequest()) {
            checkRequest();
        }
        return addRequestBuilder(RequestBuilder.upload(url, filePart));
    }

}
