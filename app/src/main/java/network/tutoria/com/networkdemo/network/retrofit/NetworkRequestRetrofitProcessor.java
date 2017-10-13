package network.tutoria.com.networkdemo.network.retrofit;

import android.content.Context;

import org.junit.Assert;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import network.tutoria.com.networkdemo.network.util.GsonUtil;
import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.RequestError;
import network.tutoria.com.networkdemo.network.api.CustomParser;
import network.tutoria.com.networkdemo.network.api.NetworkRequestProcessor;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2017/9/28 15:53.
 * Project NetWorkDemo
 * Copyright (c) 2017 zzkko Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");                                              #
 */

public class NetworkRequestRetrofitProcessor implements NetworkRequestProcessor {

    private static NetworkRequestProcessor netRequestProcessor;

    public static NetworkRequestProcessor getInstance() {
        Assert.assertNotNull("init this before use it", requestService);
        if (netRequestProcessor == null) {
            netRequestProcessor = new NetworkRequestRetrofitProcessor();
        }
        return netRequestProcessor;
    }

    private static RetrofitRequestService requestService;

    private static OkHttpClient okHttpClient;

    public static void init(Context context) {
        //执行框架 初始化 在application中调用一次
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        //不需要base url 随便填个
        retrofitBuilder.baseUrl("http://example.com/api/");
        okHttpClient = new OkHttpClient.Builder()
                .build();
        retrofitBuilder.client(okHttpClient);
        Retrofit retrofit = retrofitBuilder.build();
        requestService = retrofit.create(RetrofitRequestService.class);
    }

    private <T> T parseStringToObject(RequestBuilder requestContents, String response, Type type) throws Exception {
        CustomParser<T> customParser = requestContents.getCustomParser();
        if (customParser != null) {
            return customParser.parseResult(type, response);
        }
        return GsonUtil.getGson().fromJson(response, type);
    }

    private void onGetError(final NetworkResultHandler resultHandler, Throwable throwable, RequestBuilder requestContents) {
        requestContents.setDone(true);
        throwable.printStackTrace();
        if (throwable instanceof RequestError) {
            resultHandler.onError((RequestError) throwable);
        } else {
            resultHandler.onError(new RequestError().setError(throwable));
        }
    }

    private <T> T parseResponseBody(@NonNull ResponseBody responseBody, RequestBuilder requestContents, Type type) throws Exception {
        if (requestContents.isParsableFlag()) {
            String resultData = responseBody.string();
            try {
                return parseStringToObject(requestContents, resultData, type);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RequestError().setError(e).setRequestResult(resultData);
            }
        } else {
            //不能解析 直接返回空值了
            return null;
        }
    }

    public <T> void startGetRequest(final RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        Observable<ResponseBody> responseBodyObservable = requestService.get(requestContents.getHeaders(), requestContents.getUrl(), requestContents.getRequestParams());
        Disposable disposable = responseBodyObservable.map(new Function<ResponseBody, T>() {
            @Override
            public T apply(@NonNull ResponseBody responseBody) throws Exception {
                return parseResponseBody(responseBody, requestContents, type);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        requestContents.setDone(true);
                        resultHandler.onLoadSuccess(t);
                        //结果返回了 删除管理的请求
                        RequestManager.get().removeTag(requestContents.getTag());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        requestContents.setDone(true);
                        onGetError(resultHandler, throwable, requestContents);
                        //结果返回了 删除管理的请求
                        RequestManager.get().removeTag(requestContents.getTag());
                    }
                });
        //管理请求
        RequestManager.get().addTag(requestContents.getTag(), disposable);
    }

    @Override
    public <T> void startPostRequest(final RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        Observable<T> request = requestService.post(requestContents.getHeaders(), requestContents.getUrl(), requestContents.getRequestParams()).map(new Function<ResponseBody, T>() {
            @Override
            public T apply(@NonNull ResponseBody responseBody) throws Exception {
                return parseResponseBody(responseBody, requestContents, type);
            }
        });
        Disposable disposable = request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
                requestContents.setDone(true);
                resultHandler.onLoadSuccess(t);
                //结果返回了 删除管理的请求
                RequestManager.get().removeTag(requestContents.getTag());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                requestContents.setDone(true);
                onGetError(resultHandler, throwable, requestContents);
                //结果返回了 删除管理的请求
                RequestManager.get().removeTag(requestContents.getTag());
            }
        });
        //管理请求
        RequestManager.get().addTag(requestContents.getTag(), disposable);
    }

    @Override
    public <T> void startUploadRequest(final RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler, final Type type) {
        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        HashMap<String, String> requestParams = requestContents.getRequestParams();
        Set<String> requestParamKeys = requestParams.keySet();
        for (String requestParamKey : requestParamKeys) {
            multiPartBuilder.addFormDataPart(requestParamKey, requestParams.get(requestParamKey));
        }
        HashMap<String, File> filePart = requestContents.getFilePart();
        Set<String> fileParamKeys = filePart.keySet();
        ArrayList<UploadPostBody> uploadPostBodies = new ArrayList<>();
        long fileTotalLength = 0;
        for (String fileParamKey : fileParamKeys) {
            File file = filePart.get(fileParamKey);
            fileTotalLength += file.length();
            UploadPostBody fileUploadBody = new UploadPostBody(MediaType.parse(requestContents.getUploadFileMediaType()), file);
            multiPartBuilder.addFormDataPart(fileParamKey, file.getName(), fileUploadBody);
            uploadPostBodies.add(fileUploadBody);
        }
        Observable<T> uploadRequest = requestService.uploadFile(requestContents.getHeaders(), requestContents.getUrl(), multiPartBuilder.build().parts()).map(new Function<ResponseBody, T>() {
            @Override
            public T apply(@NonNull ResponseBody responseBody) throws Exception {
                return parseResponseBody(responseBody, requestContents, type);
            }
        });
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
        Disposable disposable = uploadRequest.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
                requestContents.setDone(true);
                resultHandler.onLoadSuccess(t);
                //结果返回了 删除管理的请求
                RequestManager.get().removeTag(requestContents.getTag());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                requestContents.setDone(true);
                onGetError(resultHandler, throwable, requestContents);
                //结果返回了 删除管理的请求
                RequestManager.get().removeTag(requestContents.getTag());
            }
        });
        //管理请求
        RequestManager.get().addTag(requestContents.getTag(), disposable);
    }


    @Override
    public <T> void startDownloadRequest(final RequestBuilder requestContents, final NetworkResultHandler<T> resultHandler) {
        final File downloadTargetFile = requestContents.getDownloadTargetFile();
        Observable<ResponseBody> responseBodyObservable = requestService.downloadFile(requestContents.getHeaders(), requestContents.getUrl(), requestContents.getRequestParams());
        final PublishProcessor<Integer> publishProcessor = PublishProcessor.create();
        Disposable disposable = responseBodyObservable.subscribeOn(Schedulers.io()).map(new Function<ResponseBody, File>() {
            @Override
            public File apply(@NonNull ResponseBody responseBody) throws Exception {
                InputStream inputStream = responseBody.byteStream();
                Source source = Okio.source(inputStream);
                Sink fileSink = Okio.sink(downloadTargetFile);
                long contentLength = responseBody.contentLength();
                if (contentLength == 0) {
                    contentLength = 1;
                }
                long downloadTotal = 0;
                long length;
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
                return downloadTargetFile;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                //结果返回了 删除管理的请求
                RequestManager.get().removeTag(requestContents.getTag());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                onGetError(resultHandler, throwable, requestContents);
                //结果返回了 删除管理的请求
                RequestManager.get().removeTag(requestContents.getTag());
            }
        });
        //管理请求
        RequestManager.get().addTag(requestContents.getTag(), disposable);
        publishProcessor.sample(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                resultHandler.onGetDownloadProgress(integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                onGetError(resultHandler, throwable, requestContents);
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
    public void cancelRequest(@NonNull RequestBuilder requestBuilder) {
        //结果返回了 删除管理的请求
        RequestManager.get().removeTag(requestBuilder.getTag(), true);
    }


}
