package network.tutoria.com.networkdemo.network.animpl;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;

import network.tutoria.com.networkdemo.network.GsonUtil;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.api.NetworkRequestProcessor;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;

/**
 * Created on 2017/9/28 15:53.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class NetworkRequestProcessorAnImpl implements NetworkRequestProcessor {


    private static NetworkRequestProcessor netRequestProcessor;

    public static void init(Context context) {
        //执行框架 初始化 在application中调用一次
        AndroidNetworking.initialize(context);
    }

    public static NetworkRequestProcessor getInstance() {
        if (netRequestProcessor == null) {
            netRequestProcessor = new NetworkRequestProcessorAnImpl();
        }
        return netRequestProcessor;
    }

    @Override
    public <T> void startGetRequest(RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, Type type) {
        HashMap<String, String> headers = requestContents.getHeaders();
        HashMap<String, String> requestParams = requestContents.getRequestParams();
        ANRequest.GetRequestBuilder getBuilder = AndroidNetworking.get(requestContents.getUrl());
        getBuilder.addQueryParameter(requestParams);
        getBuilder.addHeaders(headers);
        getBuilder.build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                parseResponseStringResult(response, resultHandler);
            }

            @Override
            public void onError(ANError anError) {
                resultHandler.onError(anError);
            }
        });
    }

    @Override
    public <T> void startPostRequest(RequestBuilder requestBuilder, final NetworkResultHandler<T> resultHandler, Type type) {
        HashMap<String, String> headers = requestBuilder.getHeaders();
        String url = requestBuilder.getUrl();
        HashMap<String, String> requestParams = requestBuilder.getRequestParams();
        ANRequest.PostRequestBuilder postRequestBuilder = AndroidNetworking.post(url);
        postRequestBuilder.addBodyParameter(requestParams);
        postRequestBuilder.addHeaders(headers);
        postRequestBuilder.build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                parseResponseStringResult(response, resultHandler);
            }

            @Override
            public void onError(ANError anError) {
                resultHandler.onError(anError);
            }
        });
    }

    @Override
    public <T> void startUploadRequest(RequestBuilder requestBuilder, final NetworkResultHandler<T> resultHandler, Type type) {
        HashMap<String, String> headers = requestBuilder.getHeaders();
        String url = requestBuilder.getUrl();
        HashMap<String, String> requestParams = requestBuilder.getRequestParams();
        ANRequest request;
        ANRequest.MultiPartBuilder uploadBuilder = AndroidNetworking.upload(url);
        uploadBuilder.addMultipartFile(requestBuilder.getFilePart());
        uploadBuilder.addMultipartParameter(requestParams);
        uploadBuilder.addHeaders(headers);
        request = uploadBuilder.build();
        request.setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                int uploadProgress = (int) (bytesUploaded * 100 / totalBytes);
                resultHandler.onGetUploadProgress(uploadProgress);
            }
        });
        request.getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                //解析string
                parseResponseStringResult(response, resultHandler);
            }

            @Override
            public void onError(ANError anError) {
                resultHandler.onError(anError);
            }
        });
    }

    private <T> void parseResponseStringResult(String response, final NetworkResultHandler<T> resultHandler) {
        //TODO 用rxjava切换到子线程中解析
        try {
            T result = parseStringToObject(response);
            resultHandler.onLoadSuccess(result);
        } catch (Exception e) {
            e.printStackTrace();
            resultHandler.onError(e);
        }
    }

    private <T> T parseStringToObject(String response) throws Exception {
        return GsonUtil.getGson().fromJson(response, new TypeToken<T>() {
        }.getType());
    }

    @Override
    public <T> void startDownloadRequest(RequestBuilder requestBuilder, final NetworkResultHandler<T> resultHandler) {
        HashMap<String, String> headers = requestBuilder.getHeaders();
        String url = requestBuilder.getUrl();
        final File downloadTargetFile = requestBuilder.getDownloadTargetFile();
        AndroidNetworking.download(url, downloadTargetFile.getParentFile().getAbsolutePath(), downloadTargetFile.getName())
                .addHeaders(headers).setTag(requestBuilder)
                .setPriority(Priority.MEDIUM)
                .setPercentageThresholdForCancelling(50) // even if at the time of cancelling it will not cancel if 50%
                .build()                                 // downloading is done.But can be cancalled with forceCancel.
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        int uploadProgress = (int) (bytesDownloaded * 100 / totalBytes);
                        resultHandler.onGetDownloadProgress(uploadProgress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        resultHandler.onDownloadSuccess(downloadTargetFile);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        resultHandler.onError(error);
                    }
                });
    }

    //请求
    //返回数据
    @Override
    public void cancelRequest(RequestBuilder requestBuilder) {
        AndroidNetworking.cancel(requestBuilder);
    }


}
