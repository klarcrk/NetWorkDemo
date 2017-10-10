package network.tutoria.com.networkdemo.other.okhttp;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import network.tutoria.com.networkdemo.network.GsonUtil;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.RequestError;
import network.tutoria.com.networkdemo.network.api.NetworkRequestProcessor;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created on 2017/9/28 15:53.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class NetworkRequestProcessorOkHttp implements NetworkRequestProcessor {


    private static NetworkRequestProcessor netRequestProcessor;

    public static NetworkRequestProcessor getInstance() {
        Assert.assertNotNull("init this before use it", okHttpClient);
        if (netRequestProcessor == null) {
            netRequestProcessor = new NetworkRequestProcessorOkHttp();
        }
        return netRequestProcessor;
    }

    private static OkHttpClient okHttpClient;

    public static void init(Context context) {
        //执行框架 初始化 在application中调用一次
        okHttpClient = new OkHttpClient();
    }

    private void checkIsInitialed() {
        Assert.assertNotNull(okHttpClient);
    }

    private <T> void parseResponseStringResult(final String response, final NetworkResultHandler<T> resultHandler) {
        Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<T> e) throws Exception {
                try {
                    T result = parseStringToObject(response);
                    e.onNext(result);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    resultHandler.onError(new RequestError().setError(e1).setRequestResult(response));
                }
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        resultHandler.onLoadSuccess(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        resultHandler.onError(new RequestError().setError(throwable));
                    }
                });
    }

    private <T> T parseStringToObject(String response) throws Exception {
        return GsonUtil.getGson().fromJson(response, new TypeToken<T>() {
        }.getType());
    }

    private okhttp3.Request addRequestHeader(okhttp3.Request.Builder requestBuilder, RequestBuilder requestContents) {
        HashMap<String, String> headers = requestContents.getHeaders();
        Set<String> headerKeys = headers.keySet();
        for (String headerKey : headerKeys) {
            requestBuilder.header(headerKey, headers.get(headerKey));
        }
        return requestBuilder.build();
    }

    public <T> void startGetRequest(RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(requestContents.getGetMethodUrlWithParam());
        addRequestHeader(requestBuilder, requestContents);
        okhttp3.Request request = requestBuilder
                .get()
                .tag(requestContents)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Flowable.just(e).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<IOException>() {
                    @Override
                    public void accept(IOException e) throws Exception {
                        resultHandler.onError(new RequestError().setError(e));
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                //非ui线程
                parseResponseStringResult(response.body().string(), resultHandler);
            }
        });
    }

    @Override
    public <T> void startPostRequest(RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(requestContents.getUrl());
        addRequestHeader(requestBuilder, requestContents);
        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        HashMap<String, String> requestParams = requestContents.getRequestParams();
        Set<String> requestParamKeys = requestParams.keySet();
        for (String requestParamKey : requestParamKeys) {
            multiPartBuilder.addFormDataPart(requestParamKey, requestParams.get(requestParamKey));
        }
        RequestBody requestBody = multiPartBuilder.build();
        okhttp3.Request request = requestBuilder
                .post(requestBody)
                .tag(requestContents)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Flowable.just(e).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<IOException>() {
                    @Override
                    public void accept(IOException e) throws Exception {
                        resultHandler.onError(new RequestError().setError(e));
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                //非ui线程
                parseResponseStringResult(response.body().string(), resultHandler);
            }
        });
    }

    @Override
    public <T> void startUploadRequest(RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(requestContents.getUrl());
        addRequestHeader(requestBuilder, requestContents);

        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        HashMap<String, String> requestParams = requestContents.getRequestParams();
        Set<String> requestParamKeys = requestParams.keySet();
        for (String requestParamKey : requestParamKeys) {
            multiPartBuilder.addFormDataPart(requestParamKey, requestParams.get(requestParamKey));
        }
        HashMap<String, File> filePart = requestContents.getFilePart();
        Set<String> fileParamKeys = requestParams.keySet();
        ArrayList<UploadPostBody> uploadPostBodies = new ArrayList<>();
        long fileTotalLength = 0;
        for (String fileParamKey : fileParamKeys) {
            File file = filePart.get(fileParamKey);
            fileTotalLength += file.length();
            //RequestBody fileUploadBody = RequestBody.create(MediaType.parse(requestContents.getUploadFileMediaType()), file);
            UploadPostBody fileUploadBody = new UploadPostBody(MediaType.parse(requestContents.getUploadFileMediaType()), file);
            uploadPostBodies.add(fileUploadBody);
            multiPartBuilder.addFormDataPart(fileParamKey, file.getName(), fileUploadBody);
        }
        if (fileTotalLength == 0) {
            fileTotalLength = 1;
        }
        final long totalSize = fileTotalLength;
        final PublishProcessor<Long> publishProcessor = PublishProcessor.create();
        ProgressChangedListener progressChangedListener = new ProgressChangedListener() {
            private long readTotal = 0;

            @Override
            public void onProgressChanged(long byteTotalCount, long remainByteCount, long readCount, long readThisTime) {
                readTotal += readThisTime;
                if (!publishProcessor.hasComplete()) {
                    publishProcessor.onNext(readTotal);
                }
            }
        };
        publishProcessor.sample(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                int progress = (int) (aLong * 100 / totalSize);
                if (progress >= 100) {
                    publishProcessor.onComplete();
                }
                resultHandler.onGetUploadProgress(progress);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        });
        for (UploadPostBody uploadPostBody : uploadPostBodies) {
            uploadPostBody.setListener(progressChangedListener);
        }
        RequestBody requestBody = multiPartBuilder.build();
        okhttp3.Request request = requestBuilder
                .post(requestBody)
                .tag(requestContents)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Flowable.just(e).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<IOException>() {
                    @Override
                    public void accept(IOException e) throws Exception {
                        resultHandler.onError(new RequestError().setError(e));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        resultHandler.onError(new RequestError().setError(throwable));
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                //非ui线程
                parseResponseStringResult(response.body().string(), resultHandler);
            }
        });
    }


    @Override
    public <T> void startDownloadRequest(RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler) {
        final File downloadTargetFile = requestContents.getDownloadTargetFile();
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(requestContents.getGetMethodUrlWithParam());
        addRequestHeader(requestBuilder, requestContents);
        okhttp3.Request request = requestBuilder
                .get()
                .tag(requestContents)
                .build();
        final PublishProcessor<Integer> publishProcessor = PublishProcessor.create();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Flowable.just(e).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<IOException>() {
                    @Override
                    public void accept(IOException e) throws Exception {
                        resultHandler.onError(new RequestError().setError(e));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        resultHandler.onError(new RequestError().setError(throwable));
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                //非ui线程
                ResponseBody responseBody = response.body();
                InputStream inputStream = responseBody.byteStream();
                Source source = Okio.source(inputStream);
                Sink fileSink = Okio.sink(downloadTargetFile);
                long contentLength = responseBody.contentLength();
                if (contentLength == 0) {
                    contentLength = 1;
                }
                long downloadTotal = 0;
                long length = 0;
                Buffer buffBytes = new Buffer();
                while ((length = source.read(buffBytes, 2048)) != -1) {
                    fileSink.write(buffBytes, length);
                    downloadTotal += length;
                    int downloadProgress = (int) (downloadTotal * 100 / contentLength);
                    if (downloadProgress < 0 || downloadProgress > 100) {
                        downloadProgress = 100;
                    }
                    publishProcessor.onNext(downloadProgress);
                }
                publishProcessor.onComplete();
                fileSink.flush();
                fileSink.close();
                source.close();
                inputStream.close();
            }
        });
        publishProcessor.sample(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                resultHandler.onGetDownloadProgress(integer);
            }

        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                resultHandler.onError(new RequestError().setError(throwable));
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                resultHandler.onDownloadSuccess(downloadTargetFile);
            }
        });
    }

    //根据RequestBuilder取消请求
    @Override
    public void cancelRequest(@android.support.annotation.NonNull RequestBuilder requestBuilder) {
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (requestBuilder == call.request().tag()) {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (requestBuilder == call.request().tag()) {
                call.cancel();
            }
        }
    }


}
