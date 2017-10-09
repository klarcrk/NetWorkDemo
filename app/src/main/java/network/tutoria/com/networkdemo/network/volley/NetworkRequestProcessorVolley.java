package network.tutoria.com.networkdemo.network.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

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

public class NetworkRequestProcessorVolley implements NetworkRequestProcessor {


    private static NetworkRequestProcessor netRequestProcessor;

    public static NetworkRequestProcessor getInstance() {
        Assert.assertNotNull("init this before use it", queue);
        if (netRequestProcessor == null) {
            netRequestProcessor = new NetworkRequestProcessorVolley();
        }
        return netRequestProcessor;
    }

    private static RequestQueue queue;

    public static void init(Context context) {
        //执行框架 初始化 在application中调用一次
        queue = Volley.newRequestQueue(context);
    }

    private void checkIsInitialed() {
        Assert.assertNotNull(queue);
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
    public <T> NetworkRequestProcessor startGetRequest(RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        final HashMap<String, String> headers = requestContents.getHeaders();
        StringRequest request = new StringRequest(Request.Method.GET, requestContents.getGetMethodUrlWithParam(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseResponseStringResult(response, resultHandler);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultHandler.onError(error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        request.setTag(requestContents);
        return this;
    }

    @Override
    public <T> NetworkRequestProcessor startPostRequest(RequestBuilder requestBuilder, final NetworkResultHandler<T> resultHandler, final Type type) {
        final HashMap<String, String> headers = requestBuilder.getHeaders();
        final HashMap<String, String> requestParams = requestBuilder.getRequestParams();
        StringRequest request = new StringRequest(Request.Method.GET, requestBuilder.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseResponseStringResult(response, resultHandler);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultHandler.onError(error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestParams;
            }
        };
        request.setTag(requestBuilder);
        return this;
    }

    @Override
    public <T> NetworkRequestProcessor startUploadRequest(RequestBuilder requestBuilder, final NetworkResultHandler<T> resultHandler, final Type type) {
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
        return this;
    }


    @Override
    public <T> NetworkRequestProcessor startDownloadRequest(RequestBuilder requestBuilder, final NetworkResultHandler<T> resultHandler) {
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
        return this;
    }

    //请求
    //返回数据
    @Override
    public NetworkRequestProcessor cancelRequest(RequestBuilder requestBuilder) {
        AndroidNetworking.cancel(requestBuilder);
        return this;
    }


}
